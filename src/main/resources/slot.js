/*
 * Copyright (c) 2020 C4
 *
 * This file is part of Polymorph, a mod made for Minecraft.
 *
 * Polymorph is free software: you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Polymorph is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Polymorph.  If not, see <https://www.gnu.org/licenses/>.
 */

function initializeCoreMod() {
  return {
    'coremodone': {
      'target': {
        'type': 'CLASS',
        'name': 'net.minecraft.inventory.container.Slot'
      },
      'transformer': function (classNode) {
        print("Initializing transformation ", classNode.toString());
        var opcodes = Java.type('org.objectweb.asm.Opcodes');
        var MethodInsnNode = Java.type(
            'org.objectweb.asm.tree.MethodInsnNode');
        var methods = classNode.methods;

        for (m in methods) {
          var method = methods[m];
          var code = method.instructions;
          var instr = code.toArray();

          if (method.name === "onSlotChanged" || method.name
              === "func_75218_e") {
            print("Found method onSlotChanged ", method.toString());

            if (instr.length > 0) {
              var inst = instr[0];
              print("Found node ", inst.toString());
              code.insert(inst, new MethodInsnNode(opcodes.INVOKESTATIC,
                  "top/theillusivec4/polymorph/common/PolymorphHooks",
                  "onSlotChanged", "()V", false));
            }
            break;
          }
        }
        return classNode;
      }
    }
  }
}