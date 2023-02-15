package com.example.medicatrack.utilities;

import android.content.Context;
import android.content.res.Resources;

import androidx.core.graphics.drawable.DrawableCompat;

import com.example.medicatrack.R;
import com.example.medicatrack.model.Medicamento;
import com.example.medicatrack.model.enums.Forma;
import com.example.medicatrack.model.enums.Frecuencia;

public final class ResourcesUtility
{

    private ResourcesUtility(){};

    public static String enumToText(Frecuencia frecuencia)
    {
        switch (frecuencia)
        {
            case NECESIDAD:
                return "En necesidad";
            case TODOS_DIAS:
                return "Todos los días";
            case DIAS_ESPECIFICOS:
                return "Días específicos";
            case INTERVALO_REGULAR:
                return "Intervalo regular";
        }
        return null;
    }

    public static int getMedicamentoImage(Medicamento medicamento)
    {
        switch (medicamento.getForma())
        {
            case CREMA:
                switch (medicamento.getColor())
                {
                    case AZUL:
                        return R.drawable.crema_azul;
                    case ROJO:
                        return R.drawable.crema_rojo;
                    case VERDE:
                        return R.drawable.crema_verde;
                    case NARANJA:
                        return R.drawable.crema_naranja;
                    case CELESTE:
                        return R.drawable.crema_celeste;
                    case AMARILLO:
                        return R.drawable.crema_amarillo;
                }
            case LIQUIDO:
                switch (medicamento.getColor())
                {
                    case AZUL:
                        return R.drawable.liquido_azul;
                    case ROJO:
                        return R.drawable.liquido_rojo;
                    case VERDE:
                        return R.drawable.liquido_verde;
                    case NARANJA:
                        return R.drawable.liquido_naranja;
                    case CELESTE:
                        return R.drawable.liquido_celeste;
                    case AMARILLO:
                        return R.drawable.liquido_amarillo;
                }
            case PASTILLA:
                switch (medicamento.getColor())
                {
                    case AZUL:
                        return R.drawable.pastilla_azul;
                    case ROJO:
                        return R.drawable.pastilla_rojo;
                    case VERDE:
                        return R.drawable.pastilla_verde;
                    case NARANJA:
                        return R.drawable.pastilla_naranja;
                    case CELESTE:
                        return R.drawable.pastilla_celeste;
                    case AMARILLO:
                        return R.drawable.pastilla_amarillo;
                }
            case REDONDO:
                switch (medicamento.getColor())
                {
                    case AZUL:
                        return R.drawable.redondo_azul;
                    case ROJO:
                        return R.drawable.redondo_rojo;
                    case VERDE:
                        return R.drawable.redondo_verde;
                    case NARANJA:
                        return R.drawable.redondo_naranja;
                    case CELESTE:
                        return R.drawable.redondo_celeste;
                    case AMARILLO:
                        return R.drawable.redondo_amarillo;
                }
            case PILDORA:
                switch (medicamento.getColor())
                {
                    case AZUL:
                        return R.drawable.pildora_azul;
                    case ROJO:
                        return R.drawable.pildora_rojo;
                    case VERDE:
                        return R.drawable.pildora_verde;
                    case NARANJA:
                        return R.drawable.pildora_naranja;
                    case CELESTE:
                        return R.drawable.pildora_celeste;
                    case AMARILLO:
                        return R.drawable.pildora_amarillo;
                }
        }
        return 0;
    }

}
