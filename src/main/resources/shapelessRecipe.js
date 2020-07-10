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
        'name': 'net.minecraft.item.crafting.ShapelessRecipe'
      },
      'transformer': function (classNode) {
        print("Initializing transformation ", classNode.toString());
        var opcodes = Java.type('org.objectweb.asm.Opcodes');
        var MethodInsnNode = Java.type(
            'org.objectweb.asm.tree.MethodInsnNode');
        var VarInsnNode = Java.type('org.objectweb.asm.tree.VarInsnNode');
        var methods = classNode.methods;

        for (m in methods) {
          var method = methods[m];

          if (method.name === "<init>") {
            print("Found method <init> ", method.toString());
            var code = method.instructions;
            var instr = code.toArray();
            var count = 0;

            for (var i = 0; i < instr.length; i++) {
              var instruction = instr[i];

              if (instruction.getOpcode() === opcodes.PUTFIELD) {
                count++;

                if (count >= 5) {
                  var inst = instruction;
                  print("Found node ", inst.toString());
                  code.insert(inst,
                      new MethodInsnNode(opcodes.INVOKESTATIC,
                          "top/theillusivec4/polymorph/common/PolymorphHooks",
                          "packIngredients",
                          "(Lnet/minecraft/item/crafting/ShapelessRecipe;)V",
                          false));
                  code.insert(inst, new VarInsnNode(opcodes.ALOAD, 0));
                  break;
                }
              }
            }
          }
        }
        return classNode;
      }
    }
  }
}
