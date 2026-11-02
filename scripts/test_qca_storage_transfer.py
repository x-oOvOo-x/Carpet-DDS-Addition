#!/usr/bin/env python3
"""Execute QCA's actual empty-slot branch against capacity-limited Slot doubles.

Minecraft itself is not launched: only ItemStack/Slot/Container are replaced.
The transaction and extraction bodies are read from production on every run,
so removing the production limit makes these quantity assertions fail.
"""
import shutil
import subprocess
import tempfile
import unittest
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SOURCE = ROOT / "src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessStorage.java"


def block(text, marker):
    start = text.index(marker)
    opening = text.index("{", start)
    depth = 1
    end = opening + 1
    while depth:
        depth += (text[end] == "{") - (text[end] == "}")
        end += 1
    return text[start:end], text[opening + 1:end - 1]


class QcaStorageTransferTest(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        if not shutil.which("java"):
            raise RuntimeError("A JDK with the Java source launcher is required")
        source = SOURCE.read_text(encoding="utf-8")
        _, branch = block(source, "if (target.isEmpty())")
        extraction, _ = block(source, "private static ItemStack extractLast(")
        cls.directory = tempfile.TemporaryDirectory(prefix="dds-qca-capacity-")
        cls.harness = Path(cls.directory.name) / "QcaStorageTransferProbe.java"
        cls.harness.write_text("""
public class QcaStorageTransferProbe {
    static final class ItemStack {
        static final ItemStack EMPTY = new ItemStack(0, 64);
        int count;
        final int max;
        ItemStack(int count, int max) { this.count = count; this.max = max; }
        boolean isEmpty() { return count == 0; }
        int getCount() { return count; }
        int getMaxStackSize() { return max; }
        ItemStack split(int amount) {
            int moved = Math.min(count, amount);
            count -= moved;
            return new ItemStack(moved, max);
        }
    }
    static final class Container {
        final ItemStack[] items;
        int writes;
        Container(ItemStack... items) { this.items = items; }
        ItemStack getItem(int i) { return items[i]; }
        void setItem(int i, ItemStack value) { items[i] = value; }
        void setChanged() { writes++; }
        int total() {
            int total = 0;
            for (ItemStack item : items) total += item.count;
            return total;
        }
    }
    static final class StorageHandle {
        final Container container;
        final int size;
        StorageHandle(Container container) { this.container = container; size = container.items.length; }
    }
    static final class Slot {
        final int limit;
        final boolean allowed;
        ItemStack item = ItemStack.EMPTY;
        int writes;
        Slot(int limit, boolean allowed) { this.limit = limit; this.allowed = allowed; }
        boolean mayPlace(ItemStack value) { return allowed; }
        int getMaxStackSize(ItemStack value) { return limit; }
        void set(ItemStack value) { item = value; }
        void setChanged() { writes++; }
    }
    // Legacy rejection branch rollback; the fixed extraction checks before any mutation.
    static int insert(StorageHandle storage, ItemStack value) {
        for (int i = 0; i < storage.size; i++) {
            if (storage.container.items[i].isEmpty()) {
                storage.container.items[i] = value;
                return value.count;
            }
        }
        throw new AssertionError("Unexpected rollback without a free source slot");
    }
    static boolean emptyDestination(StorageHandle carriedStorage, Slot slot) {
""" + branch + "\n    }\n" + extraction + """
    public static void main(String[] args) {
        int cap = Integer.parseInt(args[0]);
        int count = Integer.parseInt(args[1]);
        int itemMax = Integer.parseInt(args[2]);
        boolean allowed = Boolean.parseBoolean(args[3]);
        Container container = new Container(new ItemStack(7, 64), new ItemStack(count, itemMax));
        // Empty storage is tested separately; the other cases also protect last-first ordering.
        if (count == 0) container.items[0] = ItemStack.EMPTY;
        int before = container.total();
        Slot slot = new Slot(cap, allowed);
        if (!emptyDestination(new StorageHandle(container), slot))
            throw new AssertionError("Storage click should be consumed");
        int expected = allowed ? Math.min(count, Math.max(0, Math.min(cap, itemMax))) : 0;
        if (slot.item.count != expected)
            throw new AssertionError("destination=" + slot.item.count + " expected=" + expected + " cap=" + cap);
        if (before != container.total() + slot.item.count)
            throw new AssertionError("Quantity was not conserved");
        if (count > 0 && container.items[0].count != 7)
            throw new AssertionError("Extraction reordered the earlier source stack");
        if (expected == 0 && (container.writes != 0 || slot.writes != 0))
            throw new AssertionError("Rejected/empty extraction mutated storage");
        System.out.println("source=" + container.total() + " destination=" + slot.item.count + " cap=" + cap);
    }
}
""", encoding="utf-8")

    @classmethod
    def tearDownClass(cls):
        cls.directory.cleanup()

    def check_transfer(self, capacity, count=64, item_max=64, allowed=True):
        result = subprocess.run(
            ["java", str(self.harness), str(capacity), str(count), str(item_max), str(allowed).lower()],
            capture_output=True, text=True, timeout=30,
        )
        self.assertEqual(0, result.returncode, result.stdout + result.stderr)

    def test_slot_capacity_one_keeps_remainder(self):
        self.check_transfer(1)

    def test_slot_capacity_sixteen_keeps_remainder(self):
        self.check_transfer(16)

    def test_full_stack_and_item_limit(self):
        self.check_transfer(64)
        self.check_transfer(64, 16, 16)

    def test_rejected_and_zero_capacity_do_not_mutate(self):
        self.check_transfer(64, allowed=False)
        self.check_transfer(0)

    def test_empty_storage_is_unchanged(self):
        self.check_transfer(64, count=0)


if __name__ == "__main__":
    unittest.main()
