package com.ocado.basket;


import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {

        BasketSplitter splitter=new BasketSplitter("Absolute Path");

        ArrayList<String> lista = new ArrayList<>();

        System.out.println(splitter.split(lista));

    }
}