/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package icook;

import javafx.scene.control.TextField;

/**
 *
 * @author Ben Frailey
 */
class LimitedTextField extends TextField {

    private final int limit;

    public LimitedTextField(int limit) {
        this.limit = limit;
    }

    @Override
    public void replaceText(int start, int end, String text) {
        super.replaceText(start, end, text);
        verify();
    }

    @Override
    public void replaceSelection(String text) {
        super.replaceSelection(text);
        verify();
    }

    private void verify() {
        if (getText().length() > limit) {
            char newInput = getText().charAt(0);
            char oldInput = getText().charAt(2);
            
            String newString = Character.toString(oldInput) + Character.toString(newInput);
            
            setText(newString);
        }

    }
}