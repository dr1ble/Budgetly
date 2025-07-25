package shmr.budgetly.di.features.colorpicker

import androidx.lifecycle.ViewModel
import dagger.Binds
import dagger.Module
import dagger.multibindings.IntoMap
import shmr.budgetly.di.viewmodel.ViewModelKey
import shmr.budgetly.ui.screens.settings.colorpicker.ColorPickerViewModel

@Module
abstract class ColorPickerModule {
    @Binds
    @IntoMap
    @ViewModelKey(ColorPickerViewModel::class)
    abstract fun bindColorPickerViewModel(viewModel: ColorPickerViewModel): ViewModel
}