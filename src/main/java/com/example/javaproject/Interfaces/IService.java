package com.example.javaproject.Interfaces;

import java.util.List;

public interface IService<T>{
    public void addEntity(T t);
    public void deleteEntity(T t);
    public void updateEntity(T t);

    public List<T> getAllData();
}