package ru.mkedonsky.lesson5;

import dagger.Component;

@Component
public interface AppComponent {
    void inject(MainActivity mainActivity);
    SugarComponent getSugarComponent(DaggerSugar module);
}
