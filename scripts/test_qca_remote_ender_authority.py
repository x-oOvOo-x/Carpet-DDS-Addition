#!/usr/bin/env python3
"""Execute production SourceRef on the Java source launcher without Minecraft jars."""
from pathlib import Path
import re
import json
import shutil
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
SESSION = ROOT / 'src/main/java/carpetddsaddition/feature/quickcontaineraccess/compat/QuickContainerAccessSession.java'


def member(text, signature):
    start = text.index(signature)
    brace = text.index('{', start)
    depth = 1
    end = brace + 1
    while depth:
        depth += (text[end] == '{') - (text[end] == '}')
        end += 1
    return text[start:end]


def preprocess(text, mc):
    """Select latest MC branch of ReplayMod directives for the extracted member."""
    active, stack, out = True, [], []
    for line in text.splitlines():
        match = re.match(r'\s*//\s*#(if|elseif|else|endif)\b(.*)', line)
        if match:
            kind, expression = match.groups()
            if kind in ('if', 'elseif'):
                condition = eval(expression.replace('MC', str(mc)).replace('&&', ' and ').replace('||', ' or '))
            if kind == 'if':
                stack.append((active, condition))
                active = active and condition
            elif kind == 'elseif':
                parent, seen = stack[-1]
                active = parent and not seen and condition
                stack[-1] = (parent, seen or condition)
            elif kind == 'else':
                parent, seen = stack[-1]
                active = parent and not seen
                stack[-1] = (parent, True)
            else:
                active = stack.pop()[0]
        elif active:
            out.append(re.sub(r'^(\s*)//\$\$ ?', r'\1', line))
    return '\n'.join(out)


HARNESS = r'''
package fixture;
import java.util.*;
public class AuthorityHarness {
  static final Map<ItemStack,ServerPlayer> SOURCE_LEASES = new IdentityHashMap<>();
  static Inventory inventory(ServerPlayer p) { return p.inventory; }
  static int containerSlot(Slot s) { return s.index; }
  static int offhandSlot() { return 40; }
  static String enumName(Object o) { return String.valueOf(o); }
  static Container canonicalContainer(Container c) { return c; }
  static ServerPlayer ownerOf(Object c) { return c instanceof Container ? ((Container)c).owner : null; }
  static DdsEnderMenuFactory enderMenuFactory(Object o) { return o instanceof DdsEnderMenuFactory ? (DdsEnderMenuFactory)o : null; }
  static int actualRemoteEnderSize(ServerPlayer p) { return p.ender.size; }
  // Production nested class, not a reimplementation of its authority predicates.
  SOURCE_REF
  PROVIDER_HELPER
  static SourceRef capture(ServerPlayer p, ServerPlayer o, AbstractContainerMenu m, int size) throws Exception {
    for (java.lang.reflect.Method method : SourceRef.class.getDeclaredMethods()) {
      if (!method.getName().equals("remoteEnderGuard")) continue;
      method.setAccessible(true);
      if (method.getParameterCount() == 4) return (SourceRef)method.invoke(null,p,o,m,size);
      return (SourceRef)method.invoke(null,p,o,size);
    }
    throw new AssertionError("missing remote guard factory");
  }
  static List<String> failures = new ArrayList<>();
  static void check(boolean value, String name) { if (!value) failures.add(name); }
  static SourceRef fresh(ServerPlayer p, ServerPlayer o, AbstractContainerMenu m) throws Exception {
    m.valid=true; DdsGcaFakePlayerBridge.enabled=true; PlayerInventoryExtension.enabled=true;
    p.server.online=o; return capture(p,o,m,27);
  }
  public static void main(String[] args) throws Exception {
    ServerPlayer p=new ServerPlayer(), o=new ServerPlayer();
    AbstractContainerMenu menu=new AbstractContainerMenu();
    o.ender=new PlayerEnderChestContainer(); o.ender.owner=o;
    menu.slots.add(new Slot(o.ender,0));
    SourceRef ref=fresh(p,o,menu);
    check(ref.valid() && ref.enderAuthorityValid(),"valid initial provider grant");
    p.containerMenu=new AbstractContainerMenu();
    check(ref.valid() && ref.enderAuthorityValid(),"normal source-menu close must remain usable");
    menu.valid=false;
    check(!ref.valid(),"invalid authorizing parent must reject synthetic session");
    if (MC_VERSION >= 11904) {
    ref=fresh(p,o,menu); DdsGcaFakePlayerBridge.enabled=false;
    check(!ref.valid() && !ref.enderAuthorityValid(),"revoked GCA Ender rule must reject synthetic session");
    DdsGcaFakePlayerBridge.enabled=true;
    check(!ref.enderAuthorityValid(),"revoked provider grant must stay revoked");
    }
    ref=fresh(p,o,menu); p.server.online=new ServerPlayer();
    check(!ref.valid(),"replacement owner instance must reject");
    if (MC_VERSION >= 12111) check(ref.enderMenuFactory != null,"reuse full-capacity canonical Provider policy factory");
    o.ender.size=54; ref=fresh(p,o,menu);
    check(ref.enderMenuFactory == null,"27-slot grant must not select a 54-slot canonical factory");
    o.ender.size=27;
    ref=fresh(p,o,menu); o.ender.size=54;
    check(!ref.valid(),"capacity change must reject captured authority"); o.ender.size=27;
    ref=fresh(p,o,menu); Container original=o.ender; o.ender=new PlayerEnderChestContainer();
    check(!ref.valid(),"canonical container replacement must reject"); o.ender=original;
    ref=fresh(p,o,menu); p.connection=new Object();
    check(!ref.valid(),"replacement viewer connection must reject");
    ref=fresh(p,o,menu); PlayerInventoryExtension.enabled=false;
    check(!ref.valid() && !ref.enderAuthorityValid(),"Extension OFF must revoke synthetic authority");
    PlayerInventoryExtension.enabled=true;
    check(!ref.enderAuthorityValid(),"Extension revocation stays sticky");
    SourceRef ordinary=SourceRef.guard(p,o,null,null,null,0,false);
    PlayerInventoryExtension.enabled=false;
    check(ordinary.valid() && ordinary.enderAuthorityValid(),"ordinary QCA guard is independent of Extension");
    if (!failures.isEmpty()) throw new AssertionError(String.join("; ",failures));
    System.out.println("PASS: synthetic authority lifecycle, identity/capacity, ordinary QCA independence");
  }
}
class PlayerInventoryExtension { static boolean enabled=true; static boolean enabled(){return enabled;} }
class DdsGcaFakePlayerBridge { static boolean enabled=true; static boolean isGcaEnderChestRuleEnabled(){return enabled;} }
class DdsPlayerInventoryCanonicalEnderMenu { static DdsEnderMenuFactory captureFactory(ServerPlayer p,AbstractContainerMenu m,ServerPlayer o){return new DdsEnderMenuFactory(){};} }
interface DdsEnderMenuFactory {}
class PathNode {}
class ItemStack { static final ItemStack EMPTY=new ItemStack(); boolean isEmpty(){return true;} int getCount(){return 1;} }
class QuickContainerAccessItems {
  static boolean isShulker(ItemStack s){return false;} static boolean isAnvil(ItemStack s){return false;}
  static boolean sameItemAndData(ItemStack a,ItemStack b){return a==b;}
}
class ServerPlayer {
  Object connection=new Object(); Container ender=new Container(); Inventory inventory=new Inventory();
  AbstractContainerMenu containerMenu; Server server=new Server(); Server level=server;
  boolean isAlive(){return true;} Container getEnderChestInventory(){return ender;}
  Server serverLevel(){return server;} Server level(){return server;} Object getUUID(){return this;}
}
class Server {
  ServerPlayer online; Server getServer(){return this;} Server getPlayerList(){return this;}
  ServerPlayer getPlayer(Object uuid){return online;}
}
class Container {
  int size=27; ServerPlayer owner;
  int getContainerSize(){return size;} ItemStack getItem(int i){return ItemStack.EMPTY;}
  void setItem(int i,ItemStack s){} void setChanged(){}
}
class PlayerEnderChestContainer extends Container {}
class Inventory extends Container {}
class AbstractContainerMenu {
  boolean valid=true; List<Slot> slots=new ArrayList<>();
  boolean stillValid(ServerPlayer p){return valid;} Slot getSlot(int i){return slots.get(i);}
}
class Slot { Container container; int index; Slot(Container c,int i){container=c;index=i;} ItemStack getItem(){return ItemStack.EMPTY;} }
'''


class SyntheticEnderAuthorityTest(unittest.TestCase):
    def test_production_source_ref_lifecycle(self):
        self.assertIsNotNone(shutil.which('java'), 'Java 17+ source launcher is required for authority regression tests')
        source = SESSION.read_text(encoding='utf-8')
        for version in json.loads((ROOT / 'settings.json').read_text(encoding='utf-8'))['activeVersions']:
            parts = [int(v) for v in version.split('.')]
            mc = parts[0] * 10000 + parts[1] * 100 + (parts[2] if len(parts) > 2 else 0)
            with self.subTest(version=version):
                helper = ''
                if 'private static boolean remoteEnderProviderValid(' in source:
                    helper = preprocess(member(source, 'private static boolean remoteEnderProviderValid('), mc)
                java = HARNESS.replace('SOURCE_REF', preprocess(member(source, 'static final class SourceRef {'), mc)).replace('PROVIDER_HELPER', helper).replace('MC_VERSION', str(mc))
                with tempfile.TemporaryDirectory() as directory:
                    package_dir = Path(directory) / 'fixture'
                    package_dir.mkdir()
                    file = package_dir / 'AuthorityHarness.java'
                    file.write_text(java, encoding='utf-8')
                    try:
                        result = subprocess.run(
                            ['java', str(file)], text=True, encoding='utf-8',
                            capture_output=True, timeout=120,
                        )
                    except subprocess.TimeoutExpired as exc:
                        self.fail(
                            f'MC {version}: Java source launcher timed out after 120s; '
                            f'stdout={exc.stdout!r}; stderr={exc.stderr!r}'
                        )
                self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
                self.assertIn('PASS: synthetic authority lifecycle', result.stdout)



if __name__ == '__main__':
    unittest.main()
