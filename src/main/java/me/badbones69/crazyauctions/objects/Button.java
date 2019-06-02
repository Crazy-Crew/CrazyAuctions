package me.badbones69.crazyauctions.objects;

public class Button {

    private String _displayName;
    private String _material;

    public Button(String displayName, String material) {
        _displayName = displayName;
        _material = material;
    }

    public String getButtonName() {
        return _displayName;
    }

    public String getMaterial() {
        return _material;
    }

}
