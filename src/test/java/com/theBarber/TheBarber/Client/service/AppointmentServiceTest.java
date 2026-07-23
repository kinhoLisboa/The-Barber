package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import com.theBarber.TheBarber.Barber.service.BarberService;
import com.theBarber.TheBarber.Client.DTO.*;
import com.theBarber.TheBarber.Client.model.Appointment;
import com.theBarber.TheBarber.Client.model.AppointmentStatus;
import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.AppointmentRepository;
import com.theBarber.TheBarber.TypeServices.model.ServiceTypes;
import com.theBarber.TheBarber.TypeServices.repository.ServiceTypeRepository;
import com.theBarber.TheBarber.event.publisher.AppointmentEventPublisher;
import com.theBarber.TheBarber.handle.BarberException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private BarberRepository barberRepository;
    @Mock
    private ServiceTypeRepository serviceTypeRepository;
    @Mock
    private AppointmentValidator appointmentValidator;
    @Mock
    private ClientService clientService;
    @Mock
    private BarberService barberService;
    @Mock
    private AppointmentEventPublisher appointmentEventPublisher;

    @InjectMocks
    private AppointmentService appointmentService;

    private Barber barber;
    private Client client;
    private Appointment appointment;
    private ServiceTypes service;
    private UUID barberId;
    private UUID clientId;
    private UUID appointmentId;
    private UUID serviceId;
    private LocalDateTime futureTime;

    @BeforeEach
    void setUp() {
        barberId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        appointmentId = UUID.randomUUID();
        serviceId = UUID.randomUUID();
        futureTime = LocalDateTime.now().plusDays(1);

        barber = new Barber();
        barber.setId(barberId);
        barber.setName("João Barbeiro");
        barber.setEmail("joao@barbearia.com");

        client = new Client();
        client.setId(clientId);
        client.setName("Carlos Cliente");
        client.setEmail("carlos@email.com");

        service = new ServiceTypes();
        service.setId(serviceId);
        service.setName("Corte");
        service.setPrice(BigDecimal.valueOf(35.00));

        appointment = new Appointment();
        appointment.setId(appointmentId);
        appointment.setBarber(barber);
        appointment.setClient(client);
        appointment.setAppointmentTime(futureTime);
        appointment.setServices(new ArrayList<>(List.of(service)));
    }

    // =========================================================
    // createAppointment
    // =========================================================
    @Nested
    @DisplayName("createAppointment")
    class CreateAppointment {

        @Test
        @DisplayName("Deve criar agendamento com sucesso")
        void deveCriarAgendamentoComSucesso() {
            when(barberService.existsBarber(barberId)).thenReturn(barber);
            when(clientService.existsClient(clientId)).thenReturn(client);
            when(serviceTypeRepository.findAllById(List.of(serviceId))).thenReturn(List.of(service));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

            AppointmentResponse response = appointmentService.createAppointment(
                    barberId, clientId, futureTime, List.of(serviceId));

            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(AppointmentStatus.PENDING);
            assertThat(response.services()).hasSize(1);
            verify(appointmentEventPublisher).publishCreated(any(Appointment.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando barbeiro não encontrado")
        void deveLancarExcecaoQuandoBarbeiroNaoEncontrado() {
            when(barberService.existsBarber(barberId))
                    .thenThrow(BarberException.build(HttpStatus.NOT_FOUND, "Barbeiro não encontrado !"));

            assertThatThrownBy(() ->
                    appointmentService.createAppointment(barberId, clientId, futureTime, List.of(serviceId)))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Barbeiro não encontrado");
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não encontrado")
        void deveLancarExcecaoQuandoClienteNaoEncontrado() {
            when(barberService.existsBarber(barberId)).thenReturn(barber);
            when(clientService.existsClient(clientId))
                    .thenThrow(BarberException.build(HttpStatus.NOT_FOUND, "Cliente não encontrado"));

            assertThatThrownBy(() ->
                    appointmentService.createAppointment(barberId, clientId, futureTime, List.of(serviceId)))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Cliente não encontrado");
        }

        @Test
        @DisplayName("Deve lançar exceção quando serviços não encontrados")
        void deveLancarExcecaoQuandoServicosNaoEncontrados() {
            when(barberService.existsBarber(barberId)).thenReturn(barber);
            when(clientService.existsClient(clientId)).thenReturn(client);
            when(serviceTypeRepository.findAllById(any())).thenReturn(List.of());

            assertThatThrownBy(() ->
                    appointmentService.createAppointment(barberId, clientId, futureTime, List.of(serviceId)))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Serviços não encontrados");
        }

        @Test
        @DisplayName("Deve publicar evento após criar agendamento")
        void devePublicarEventoAposCriarAgendamento() {
            when(barberService.existsBarber(barberId)).thenReturn(barber);
            when(clientService.existsClient(clientId)).thenReturn(client);
            when(serviceTypeRepository.findAllById(any())).thenReturn(List.of(service));
            when(appointmentRepository.save(any(Appointment.class))).thenReturn(appointment);

            appointmentService.createAppointment(barberId, clientId, futureTime, List.of(serviceId));

            verify(appointmentEventPublisher, times(1)).publishCreated(any(Appointment.class));
        }
    }

    // =========================================================
    // list
    // =========================================================
    @Nested
    @DisplayName("list")
    class ListAppointments {

        @Test
        @DisplayName("Deve retornar página de agendamentos com sucesso")
        void deveRetornarPaginaComSucesso() {
            Page<Appointment> page = new PageImpl<>(List.of(appointment));
            when(appointmentRepository.findAllWithBarberAndClient(any())).thenReturn(page);

            Page<ListResponseAppointment> result = appointmentService.list(0, 10);

            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).barberName()).isEqualTo("João Barbeiro");
            assertThat(result.getContent().get(0).clientName()).isEqualTo("Carlos Cliente");
        }

        @Test
        @DisplayName("Deve lançar exceção quando lista estiver vazia")
        void deveLancarExcecaoQuandoListaVazia() {
            Page<Appointment> emptyPage = new PageImpl<>(List.of());
            when(appointmentRepository.findAllWithBarberAndClient(any())).thenReturn(emptyPage);

            assertThatThrownBy(() -> appointmentService.list(0, 10))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Nenhum agendamento encontrado");
        }
    }

    // =========================================================
    // delete
    // =========================================================
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve deletar agendamento com sucesso")
        void deveDeletarAgendamentoComSucesso() {
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));

            appointmentService.delete(appointmentId);

            verify(appointmentRepository).save(appointment);
            verify(appointmentRepository).delete(appointment);
            assertThat(appointment.getServices()).isEmpty();
        }

        @Test
        @DisplayName("Deve lançar exceção quando agendamento não encontrado")
        void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> appointmentService.delete(appointmentId))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Agendamento não encontrado");
        }
    }

    // =========================================================
    // changeAppointmentStatus
    // =========================================================
    @Nested
    @DisplayName("changeAppointmentStatus")
    class ChangeAppointmentStatus {

        @Test
        @DisplayName("Deve lançar exceção quando agendamento não encontrado")
        void deveLancarExcecaoQuandoAgendamentoNaoEncontrado() {
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.empty());

            assertThatThrownBy(() ->
                    appointmentService.changeAppointmentStatus(appointmentId, AppointmentStatus.CONFIRMED, "joao@barbearia.com"))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Agendamento não encontrado");
        }

        @Test
        @DisplayName("Barbeiro deve conseguir mudar status do agendamento")
        void barbeiroDeveMudarStatus() {
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(barberService.isBarber("joao@barbearia.com")).thenReturn(true);
            when(appointmentRepository.save(any())).thenReturn(appointment);

            AppointmentResponse response = appointmentService.changeAppointmentStatus(
                    appointmentId, AppointmentStatus.CONFIRMED, "joao@barbearia.com");

            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(AppointmentStatus.CONFIRMED);
            verify(appointmentEventPublisher).publishConfirmed(appointment);
        }

        @Test
        @DisplayName("Cliente deve conseguir cancelar seu próprio agendamento")
        void clienteDeveCancelarSeuAgendamento() {
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(barberService.isBarber("carlos@email.com")).thenReturn(false);
            when(appointmentRepository.save(any())).thenReturn(appointment);
            doNothing().when(appointmentValidator)
                    .validateClientPermission(appointment, "carlos@email.com", AppointmentStatus.CANCELED);

            AppointmentResponse response = appointmentService.changeAppointmentStatus(
                    appointmentId, AppointmentStatus.CANCELED, "carlos@email.com");

            assertThat(response).isNotNull();
            assertThat(response.status()).isEqualTo(AppointmentStatus.CANCELED);
            verify(appointmentEventPublisher).publishCanceled(appointment);
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar mudar status de agendamento cancelado")
        void deveLancarExcecaoAoMudarStatusDeCancelado() {
            appointment.changeStatus(AppointmentStatus.CANCELED);
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(barberService.isBarber("joao@barbearia.com")).thenReturn(true);

            assertThatThrownBy(() ->
                    appointmentService.changeAppointmentStatus(
                            appointmentId, AppointmentStatus.CONFIRMED, "joao@barbearia.com"))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("cancelado");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar finalizar agendamento pendente")
        void deveLancarExcecaoAoFinalizarAgendamentoPendente() {
            // status inicial é PENDING
            when(appointmentRepository.findById(appointmentId)).thenReturn(Optional.of(appointment));
            when(barberService.isBarber("joao@barbearia.com")).thenReturn(true);

            assertThatThrownBy(() ->
                    appointmentService.changeAppointmentStatus(
                            appointmentId, AppointmentStatus.FINALIZED, "joao@barbearia.com"))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("confirmados");
        }
    }
}
