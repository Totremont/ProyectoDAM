package com.example.medicatrack.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medicatrack.R;
import com.example.medicatrack.databinding.RegistroViewlistBinding;
import com.example.medicatrack.model.Medicamento;
import com.example.medicatrack.model.Registro;
import com.example.medicatrack.model.enums.RegistroEstado;
import com.example.medicatrack.repo.RegistroRepository;
import com.example.medicatrack.utilities.FechaFormat;
import com.example.medicatrack.utilities.ResourcesUtility;
import com.google.android.material.button.MaterialButton;

import java.text.DateFormat;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;

public class RegistroAdapter extends RecyclerView.Adapter<RegistroAdapter.RegistroViewHolder>
{

    private final ArrayList<Medicamento> medicamentos = new ArrayList<>();  //Todos los medicamentos del dia

    private final SortedMap<UUID,Registro> registros = new TreeMap<UUID,Registro>();  //Registros de aquellos tomados, no tomados y pendientes

    //Si esta seleccionado un chip
    private boolean esPasado = false;
    private boolean esFuturo = false;

    private final Context context;

    public RegistroAdapter(Context fromContext)
    {
        this.context = fromContext;
    }


    @NonNull
    @Override
    public RegistroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        return new RegistroViewHolder(RegistroViewlistBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false),context);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistroViewHolder holder, int position)
    {
        holder.bind(medicamentos.get(position),
                registros.get(medicamentos.get(position).getId()),
                esPasado,esFuturo);
    }

    @Override
    public int getItemCount()
    {
        return medicamentos.size();
    }

    public void setData(ArrayList<Medicamento> medicamentos, ArrayList<Registro> registros, boolean esPasado, boolean esFuturo)
    {
        MedicamentoDifference diferenciador = new MedicamentoDifference(medicamentos,this.medicamentos);
        DiffUtil.DiffResult resultado = DiffUtil.calculateDiff(diferenciador);

        this.medicamentos.clear();
        this.medicamentos.addAll(medicamentos);
        this.registros.clear();

        this.esFuturo = esFuturo;
        this.esPasado = esPasado;

        //resultado.dispatchUpdatesTo(this);

        registros.forEach(it ->
        {
            this.registros.put(it.getMedicamento().getId(), it);
        });

        notifyDataSetChanged();


    }

    //Clase Viewholder - se encarga de mostrar los datos en la UI
    public static class RegistroViewHolder extends RecyclerView.ViewHolder
    {
        private final RegistroViewlistBinding binding;
        private final RegistroRepository registroRepo;

        private int rotationAngle = 0;


        public RegistroViewHolder(@NonNull RegistroViewlistBinding binding, @NonNull Context context)
        {
            super(binding.getRoot());
            this.binding = binding;
            registroRepo = RegistroRepository.getInstance(context);
        }

        public void bind(Medicamento medicamento, Registro registro, boolean esPasado, boolean esFuturo)
        {
            binding.tiempoTextView.setText(FechaFormat.formattedHora(medicamento.getHora()));

            if(registro.getEstado().equals(RegistroEstado.PENDIENTE))
            {
                //start, top, end, bottom argumentos
                binding.tiempoTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_bell_pending,0,0,0);
            } else binding.tiempoTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);

            binding.nombreTextView.setText(medicamento.getNombre());

            binding.iconMedicamento.setImageResource(ResourcesUtility.getMedicamentoImage(medicamento));

            StringBuilder dosis = new StringBuilder();
            dosis.append("Consumir unidad de ").append(String.format("%.2f", medicamento.getConcentracion())).append(" ")
                    .append(medicamento.getUnidad().name());

            binding.dosisTextView.setText(dosis);

            //Comprobar si la fecha y/o hora es pasada

            ZonedDateTime ahora = ZonedDateTime.now(ZoneId.of("America/Argentina/Buenos_Aires"));

            //boolean esHoraPasada = FechaFormat.greaterTime(ahora,medicamento.getHora()) > 0;

            boolean esFechaPasada = esPasado  && !esFuturo;

            setEstado(registro,esFechaPasada);
            if(!esFuturo)
            {
                binding.getRoot().setOnClickListener(view ->
                {
                    onClickOption(3,registro,esPasado);
                });
            }

            //binding.iconMedicamento

            //Listeners
            binding.tomarButton.setOnClickListener(view -> {onClickOption(0,registro,esPasado);});

            binding.noTomarButton.setOnClickListener(view -> {onClickOption(1,registro,esPasado);});

            binding.pendienteButton.setOnClickListener(view -> {onClickOption(2,registro,esPasado);});

        }
        public void onClickOption(int opcion, Registro registro, boolean esFechaPasada)   //0 tomar, 1 no tomar, 2 pendiente
        {
            binding.buttonsLayout.setVisibility(binding.buttonsLayout.getVisibility() == LinearLayoutCompat.GONE ? LinearLayoutCompat.VISIBLE : LinearLayoutCompat.GONE);
            rotationAngle = rotationAngle == 0 ? 180 : 0;  //toggle
            binding.arrow.animate().rotation(rotationAngle).setDuration(300).start();
            if(opcion < 3) {
                updateRegistro(registro, opcion);
                setEstado(registro,esFechaPasada);
            }
        }

        public void updateRegistro(Registro registro, int opcion)
        {
            registro.setEstado(RegistroEstado.values()[opcion]);
            registroRepo.update(registro,result -> {});
            if(registro.getEstado().equals(RegistroEstado.PENDIENTE))
            {
                //start, top, end, bottom argumentos
                binding.tiempoTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.icon_bell_pending,0,0,0);
            } else binding.tiempoTextView.setCompoundDrawablesRelativeWithIntrinsicBounds(0,0,0,0);
        }

        public void setEstado(Registro registro, boolean esFechaPasada)  //esFechaPasada si la hora y/o la fecha es anterior a la hora y fecha de la notificacion
                                                                        //para este medicamento
        {
            switch(registro.getEstado())
            {
                case CONFIRMADO:
                    binding.iconTomado.setImageResource(R.drawable.icon_check_checked);
                    binding.iconNoTomado.setImageResource(R.drawable.icon_close_unchecked);
                    binding.iconPendiente.setImageResource(R.drawable.icon_pending_unchecked);
                    binding.tomarButton.setVisibility(MaterialButton.GONE);
                    if(esFechaPasada) binding.pendienteButton.setVisibility(MaterialButton.GONE);
                    else binding.pendienteButton.setVisibility(MaterialButton.VISIBLE);
                    binding.noTomarButton.setVisibility(MaterialButton.VISIBLE);
                    binding.estadoTextView.setText("Tomado");
                    break;
                case CANCELADO:
                    binding.iconTomado.setImageResource(R.drawable.icon_check_unchecked);
                    binding.iconNoTomado.setImageResource(R.drawable.icon_close_checked);
                    binding.iconPendiente.setImageResource(R.drawable.icon_pending_unchecked);
                    binding.noTomarButton.setVisibility(MaterialButton.GONE);
                    if(esFechaPasada) binding.pendienteButton.setVisibility(MaterialButton.GONE);
                    else binding.pendienteButton.setVisibility(MaterialButton.VISIBLE);
                    binding.tomarButton.setVisibility(MaterialButton.VISIBLE);
                    binding.estadoTextView.setText("No tomado");
                    break;
                case PENDIENTE:
                    binding.iconTomado.setImageResource(R.drawable.icon_check_unchecked);
                    binding.iconNoTomado.setImageResource(R.drawable.icon_close_unchecked);
                    binding.iconPendiente.setImageResource(R.drawable.icon_pending_checked);
                    binding.tomarButton.setVisibility(MaterialButton.VISIBLE);
                    binding.noTomarButton.setVisibility(MaterialButton.VISIBLE);
                    if(esFechaPasada) {
                        binding.pendienteButton.setVisibility(MaterialButton.GONE);
                        binding.estadoTextView.setText("Alarma perdida");
                    }
                    else binding.pendienteButton.setVisibility(MaterialButton.VISIBLE);
                    binding.estadoTextView.setText("Por notificar");
                    break;

                case POSPUESTO:
                    binding.iconTomado.setImageResource(R.drawable.icon_check_unchecked);
                    binding.iconNoTomado.setImageResource(R.drawable.icon_close_unchecked);
                    binding.iconPendiente.setImageResource(R.drawable.icon_pending_checked);
                    binding.tomarButton.setVisibility(MaterialButton.VISIBLE);
                    binding.noTomarButton.setVisibility(MaterialButton.VISIBLE);
                    binding.pendienteButton.setVisibility(MaterialButton.GONE);
                    binding.estadoTextView.setText("Sin elección");
                    break;
            }
        }
    }


    //Obtiene las diferencias entre 2 listas distintas
    public static class MedicamentoDifference extends DiffUtil.Callback
    {

        private final ArrayList<Medicamento> listaNueva = new ArrayList<>();
        private final ArrayList<Medicamento> listaVieja = new ArrayList<>();

        public MedicamentoDifference(ArrayList<Medicamento> listaNueva, ArrayList<Medicamento> listaVieja)
        {
            this.listaNueva.addAll(listaNueva);
            this.listaVieja.addAll(listaVieja);
        }

        @Override
        public int getOldListSize() {
            return listaVieja.size();
        }

        @Override
        public int getNewListSize() {
            return listaNueva.size();
        }

        @Override       //Son las 2 instancias iguales?
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition)
        {
            Medicamento viejo = listaVieja.get(oldItemPosition);
            Medicamento nuevo = listaNueva.get(newItemPosition);
            return nuevo.equals(viejo);
        }

        @Override       //Tienen las 2 instancias los mismos datos? (visualmente en la UI)
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition)
        {
            Medicamento viejo = listaVieja.get(oldItemPosition);
            Medicamento nuevo = listaNueva.get(newItemPosition);
            //Cantidad pastillas y registro blablabla
            return FechaFormat.formattedHora(viejo.getHora()).equals(FechaFormat.formattedHora(nuevo.getHora()))
                    && viejo.getNombre().equals(nuevo.getNombre()) && viejo.getConcentracion() == nuevo.getConcentracion();
        }
    }
}