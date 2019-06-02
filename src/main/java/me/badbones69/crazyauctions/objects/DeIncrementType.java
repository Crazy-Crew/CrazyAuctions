package me.badbones69.crazyauctions.objects;

public enum DeIncrementType {

    ADD("+"),
    SUBTRACT("-");

    private DeIncrementType(String sign) {
        _sign = sign;
    }

    private final String _sign;

    public String getSign() {
        return _sign;
    }

}
