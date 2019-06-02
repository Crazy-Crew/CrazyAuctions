package me.badbones69.crazyauctions.objects;

public class DeIncrementButton extends Button {

    private final int _value;
    private final DeIncrementType _type;

    public DeIncrementButton(String displayName, String material, int value, DeIncrementType type) {
        super(displayName, material);
        _value = value;
        _type = type;
        
    }

    @Override
    public String getButtonName() {
        return super.getButtonName() + _type.getSign() + _value;
    }

    public int getValue() {
        if(_type == DeIncrementType.SUBTRACT) {
            return -_value;
        }
        return _value;
    }

}
