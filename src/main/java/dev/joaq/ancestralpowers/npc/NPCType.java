package dev.joaq.ancestralpowers.npc;

public enum NPCType {
    CLONE("clone"),
    ARTIFICIAL("artificial"),
    ASTRAL("astral"),
    GHOST("ghost"),
    GENERIC("generic");
    
    private final String id;
    
    NPCType(String id) {
        this.id = id;
    }
    
    public String getId() {
        return id;
    }
    
    public static NPCType fromString(String id) {
        for (NPCType type : values()) {
            if (type.id.equals(id)) {
                return type;
            }
        }
        return GENERIC;
    }
}
