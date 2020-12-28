package ru.mellman.conv3rter.converterMVVM;

class ConverterViewModel {
    ConverterModel model;
    
    void Input0(){
        model.InputNum(0);
    }
    void Input1(){
        model.InputNum(1);
    }
    void Input2(){
        model.InputNum(2);
    }
    void Input3(){
        model.InputNum(3);
    }
    void Input4(){
        model.InputNum(4);
    }
    void Input5(){
        model.InputNum(5);
    }
    void Input6(){
        model.InputNum(6);
    }
    void Input7(){
        model.InputNum(7);
    }
    void Input8(){
        model.InputNum(8);
    }
    void Input9(){
        model.InputNum(9);
    }
    void DeletePressed(){
        model.DelNumber();
    }
    void DotPressed(){
        model.InputDot();
    }
}
