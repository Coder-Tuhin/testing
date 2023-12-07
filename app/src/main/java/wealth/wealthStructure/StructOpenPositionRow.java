/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package wealth.wealthStructure;


import wealth.commonstructure.ModuleClass;

/**
 * @author Tapas Nayak <tapas@xtremsoftindia.com at Xtremsoft Technologies>
 */
public class StructOpenPositionRow {

    private char charName[];    //1.---Name---CHAR---40bytes---
    private int intQtyOnHand;    //2.---QtyOnHand---INT---4bytes---

    public StructOpenPositionRow(byte[] data, int dataLength) {

        int length = 40;
        int count = 0;
        charName = new char[40];

        for (int i = 0; i < length; i++) {
            charName[i] = (char) data[count];
            count++;
        }
        length = 4;
        byte[] qtyOnHand = new byte[length];

        for (int i = 0; i < length; i++) {
            qtyOnHand[i] = data[count];
            count++;
        }

        intQtyOnHand = ModuleClass.byteToint(qtyOnHand, length);

    }

    public String getName() {
        return new String(charName).trim();
    }

    public void setName(char[] value) {
        charName = value;
    }

    public int getQtyOnHand() {
        return intQtyOnHand;
    }

    public void setQtyOnHand(int value) {
        intQtyOnHand = value;
    }


}
