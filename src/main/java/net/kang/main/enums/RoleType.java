package net.kang.main.enums;

public enum RoleType {
    ADMIN("ROLE_ADMIN"), MANAGER("ROLE_MANAGER"), USER("ROLE_USER");
    String roleType;
    RoleType(String roleType){
        this.roleType = roleType;
    }
    public String getRoleType(){
        return roleType;
    }
}
