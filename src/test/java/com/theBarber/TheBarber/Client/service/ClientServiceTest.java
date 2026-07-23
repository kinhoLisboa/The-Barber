package com.theBarber.TheBarber.Client.service;

import com.theBarber.TheBarber.Client.DTO.*;
import com.theBarber.TheBarber.Client.model.Client;
import com.theBarber.TheBarber.Client.repository.ClientRepository;
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
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock
    private ClientRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ClientService clientService;

    private Client client;
    private UUID clientId;
    private ClientRequest clientRequest;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();

        client = new Client();
        client.setId(clientId);
        client.setName("Carlos Cliente");
        client.setEmail("carlos@email.com");
        client.setPhone("11999999999");

        clientRequest = new ClientRequest(
                null,
                "Carlos Cliente",
                "carlos@email.com",
                "senha123",
                "11999999999"
        );
    }

    // =========================================================
    // register
    // =========================================================
    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("Deve registrar cliente com sucesso")
        void deveRegistrarClienteComSucesso() {
            when(repository.existsByEmail(clientRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(clientRequest.password())).thenReturn("senhaCriptografada");
            when(repository.save(any(Client.class))).thenReturn(client);

            ClientResponse response = clientService.register(clientRequest);

            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("Carlos Cliente");
            assertThat(response.email()).isEqualTo("carlos@email.com");
            verify(repository).save(any(Client.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando email já cadastrado")
        void deveLancarExcecaoQuandoEmailJaCadastrado() {
            when(repository.existsByEmail(clientRequest.email())).thenReturn(true);

            assertThatThrownBy(() -> clientService.register(clientRequest))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("email ja existe");
        }

        @Test
        @DisplayName("Deve salvar senha criptografada")
        void deveSalvarSenhaCriptografada() {
            when(repository.existsByEmail(clientRequest.email())).thenReturn(false);
            when(passwordEncoder.encode("senha123")).thenReturn("senhaCriptografada");
            when(repository.save(any(Client.class))).thenReturn(client);

            clientService.register(clientRequest);

            verify(passwordEncoder).encode("senha123");
        }
    }

    // =========================================================
    // list
    // =========================================================
    @Nested
    @DisplayName("list")
    class ListClients {

        @Test
        @DisplayName("Deve retornar página de clientes com sucesso")
        void deveRetornarPaginaComSucesso() {
            Page<Client> page = new PageImpl<>(List.of(client));
            when(repository.findAll(any(Pageable.class))).thenReturn(page);

            Page<ListResponseClient> result = clientService.list(0, 10);

            assertThat(result).isNotEmpty();
            assertThat(result.getContent()).hasSize(1);
            assertThat(result.getContent().get(0).name()).isEqualTo("Carlos Cliente");
        }

        @Test
        @DisplayName("Deve lançar exceção quando lista estiver vazia")
        void deveLancarExcecaoQuandoListaVazia() {
            Page<Client> emptyPage = new PageImpl<>(List.of());
            when(repository.findAll(any(Pageable.class))).thenReturn(emptyPage);

            assertThatThrownBy(() -> clientService.list(0, 10))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Nenhum cliente encontrado");
        }
    }

    // =========================================================
    // fetch
    // =========================================================
    @Nested
    @DisplayName("fetch")
    class Fetch {

        @Test
        @DisplayName("Deve retornar cliente detalhado com sucesso")
        void deveRetornarClienteDetalhado() {
            when(repository.existsById(clientId)).thenReturn(true);
            when(repository.findById(clientId)).thenReturn(Optional.of(client));

            ClientDetailed result = clientService.fetch(clientId);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("Carlos Cliente");
            assertThat(result.email()).isEqualTo("carlos@email.com");
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não encontrado")
        void deveLancarExcecaoQuandoClienteNaoEncontrado() {
            when(repository.existsById(clientId)).thenReturn(false);

            assertThatThrownBy(() -> clientService.fetch(clientId))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Cliente não encontrado");
        }
    }

    // =========================================================
    // alter
    // =========================================================
    @Nested
    @DisplayName("alter")
    class Alter {

        @Test
        @DisplayName("Deve alterar cliente com sucesso")
        void deveAlterarClienteComSucesso() {
            UpdateClient updateClient = new UpdateClient(clientId, "Carlos Atualizado", "carlos@novo.com", "11988888888");
            when(repository.existsById(clientId)).thenReturn(true);

            clientService.alter(updateClient, clientId);

            verify(repository).save(any(Client.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não encontrado ao alterar")
        void deveLancarExcecaoQuandoClienteNaoEncontradoAoAlterar() {
            UpdateClient updateClient = new UpdateClient(clientId, "Carlos Atualizado", "carlos@novo.com", "11988888888");
            when(repository.existsById(clientId)).thenReturn(false);

            assertThatThrownBy(() -> clientService.alter(updateClient, clientId))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Cliente não encontrado");
        }
    }

    // =========================================================
    // delete
    // =========================================================
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve deletar cliente com sucesso")
        void deveDeletarClienteComSucesso() {
            when(repository.existsById(clientId)).thenReturn(true);
            when(repository.getReferenceById(clientId)).thenReturn(client);

            clientService.delete(clientId);

            verify(repository).delete(client);
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não encontrado ao deletar")
        void deveLancarExcecaoQuandoClienteNaoEncontradoAoDeletar() {
            when(repository.existsById(clientId)).thenReturn(false);

            assertThatThrownBy(() -> clientService.delete(clientId))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Cliente não encontrado");
        }
    }

    // =========================================================
    // existsClient
    // =========================================================
    @Nested
    @DisplayName("existsClient")
    class ExistsClient {

        @Test
        @DisplayName("Deve retornar cliente quando encontrado")
        void deveRetornarClienteQuandoEncontrado() {
            when(repository.findById(clientId)).thenReturn(Optional.of(client));

            Client result = clientService.existsClient(clientId);

            assertThat(result).isNotNull();
            assertThat(result.getId()).isEqualTo(clientId);
        }

        @Test
        @DisplayName("Deve lançar exceção quando cliente não encontrado")
        void deveLancarExcecaoQuandoClienteNaoEncontrado() {
            when(repository.findById(clientId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> clientService.existsClient(clientId))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Cliente não encontrado");
        }
    }
}
