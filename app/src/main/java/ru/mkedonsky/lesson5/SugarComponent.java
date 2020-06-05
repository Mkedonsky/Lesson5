package ru.mkedonsky.lesson5;

import android.os.Bundle;
import dagger.Subcomponent;
import io.reactivex.Single;

@Subcomponent(modules = DaggerSugar.class)
public interface SugarComponent {
    Single<Bundle> sugarSaveAll();
    Single<Bundle> getAll();
    Single<Bundle> deleteAll();
}
