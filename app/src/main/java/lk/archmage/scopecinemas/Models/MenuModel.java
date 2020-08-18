package lk.archmage.scopecinemas.Models;

public class MenuModel {

    public String menuName, url;
    public int menuIcon;
    public boolean isGroup;

    public MenuModel(String menuName,int menuIcon, boolean isGroup, String url) {

        this.menuName = menuName;
        this.menuIcon = menuIcon;
        this.url = url;
        this.isGroup = isGroup;
    }
}
