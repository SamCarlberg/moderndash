package edu.wpi.moderndash.action;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class UndoManagerTest {

    private UndoManager undoManager;

    @Before
    public void setUp() {
        undoManager = new UndoManager();
    }

    @Test
    public void testEmpty() {
        assertFalse("Should not be able to redo", undoManager.canRedo());
        assertFalse("Should not be able to undo", undoManager.canUndo());
        assertFalse("Undo should do nothing", undoManager.undo());
        assertFalse("Redo should do nothing", undoManager.redo());
    }

    @Test
    public void testSingle() {
        final String original = "original";
        final String change = "changed";
        String[] s = {original};
        Action a = Action.of(() -> s[0] = change,
                             () -> s[0] = original);
        a.act();
        undoManager.observe(a);
        assertFalse("Should not be able to redo", undoManager.canRedo());
        assertTrue("Should be able to undo", undoManager.canUndo());

        // Should be able to undo
        boolean undid = undoManager.undo();
        assertTrue("Undo didn't run", undid);
        assertEquals(original, s[0]);
        assertTrue("Should be able to redo", undoManager.canRedo());
        assertFalse("Should not be able to undo again", undoManager.canUndo());

        // Can't undo again
        undid = undoManager.undo();
        assertFalse("Undo should not have worked", undid);
        assertTrue("Should be able to redo", undoManager.canRedo());
        assertFalse("Should not be able to undo", undoManager.canUndo());

        // Should be able to redo
        boolean redid = undoManager.redo();
        assertTrue("Redo should have worked", redid);
        assertEquals(change, s[0]);
        assertFalse("Should not be able to redo again", undoManager.canRedo());
        assertTrue("Should be able to undo", undoManager.canUndo());
    }

    @Test
    public void testBranchingWith2() {
        Action a = Action.NOP;
        Action b = Action.NOP;
        undoManager.observe(a);
        undoManager.undo(); // we know this works due to testSingle()
        undoManager.observe(b);
        assertEquals(1, undoManager.size());
        // Everything here should work exactly the same as testSingle()
        assertTrue(undoManager.canUndo());
        assertFalse(undoManager.canRedo());
    }

    @Test
    public void testMultiple() {
        Action a = Action.NOP;
        Action b = Action.NOP;
        Action c = Action.NOP;

        undoManager.observe(a);
        undoManager.observe(b);
        assertTrue(undoManager.undo()); // undo B
        assertTrue(undoManager.undo()); // undo A (back to base state)
    }

}