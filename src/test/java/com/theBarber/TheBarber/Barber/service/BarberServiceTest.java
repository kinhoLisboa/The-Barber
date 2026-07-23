package com.theBarber.TheBarber.Barber.service;

import com.theBarber.TheBarber.Barber.DTO.*;
import com.theBarber.TheBarber.Barber.model.Address;
import com.theBarber.TheBarber.Barber.model.Barber;
import com.theBarber.TheBarber.Barber.model.Role;
import com.theBarber.TheBarber.Barber.repository.BarberRepository;
import com.theBarber.TheBarber.handle.BarberException;
import org.junit.jupiter.api.AfterEach;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BarberServiceTest {

    @Mock
    private BarberRepository repository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private BarberService barberService;

    private Barber barber;
    private UUID barberId;
    private BarberRequest adminRequest;
    private BarberRequest barbeiroRequest;

    @BeforeEach
    void setUp() {
        barberId = UUID.randomUUID();

        barber = new Barber();
        barber.setId(barberId);
        barber.setName("João Admin");
        barber.setEmail("admin@barbearia.com");
        barber.setCpf("529.982.247-25");
        barber.setPhone("11999999999");
        barber.setRole(Role.ADMIN);

        adminRequest = new BarberRequest(
                null,
                "João Admin",
                "senha123",
                "admin@barbearia.com",
                "529.982.247-25",
                "11999999999",
                Role.ADMIN,
                null
        );

        barbeiroRequest = new BarberRequest(
                null,
                "Pedro Barbeiro",
                "senha123",
                "pedro@barbearia.com",
                "529.982.247-25",
                "11988888888",
                Role.BARBEIRO,
                null
        );
    }

    @AfterEach
    void tearDown() {
        // limpa o SecurityContext após cada teste para não vazar entre testes
        SecurityContextHolder.clearContext();
    }

    // =========================================================
    // register
    // =========================================================
    @Nested
    @DisplayName("register")
    class Register {

        @Test
        @DisplayName("Deve registrar o primeiro admin com sucesso")
        void deveRegistrarPrimeiroAdminComSucesso() {
            when(repository.existsByRole(Role.ADMIN)).thenReturn(false);
            when(repository.existsByEmail(adminRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(adminRequest.password())).thenReturn("senhaCriptografada");
            when(repository.save(any(Barber.class))).thenReturn(barber);

            BarberResponse response = barberService.register(adminRequest);

            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("João Admin");
            assertThat(response.email()).isEqualTo("admin@barbearia.com");
            verify(repository).save(any(Barber.class));
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar criar barbeiro sem admin cadastrado")
        void deveLancarExcecaoAoCriarBarbeiroSemAdmin() {
            when(repository.existsByRole(Role.ADMIN)).thenReturn(false);

            assertThatThrownBy(() -> barberService.register(barbeiroRequest))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("administrador antes de criar barbeiros");
        }

        @Test
        @DisplayName("Deve lançar exceção ao tentar cadastrar segundo admin")
        void deveLancarExcecaoAoCadastrarSegundoAdmin() {
            when(repository.existsByRole(Role.ADMIN)).thenReturn(true);

            assertThatThrownBy(() -> barberService.register(adminRequest))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Já existe um administrador cadastrado");
        }

        @Test
        @DisplayName("Deve lançar exceção quando email já cadastrado")
        void deveLancarExcecaoQuandoEmailJaCadastrado() {
            // existsByRole é chamado em existsAdmin() para verificar se barbeiro pode ser criado
            // existEmail() lança a exceção antes de chegar no validateAdminCreationPermission
            // por isso não é necessário mockar o SecurityContext aqui
            when(repository.existsByRole(Role.ADMIN)).thenReturn(true);
            when(repository.existsByEmail(barbeiroRequest.email())).thenReturn(true);

            assertThatThrownBy(() -> barberService.register(barbeiroRequest))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Já existe um barbeiro com este e-mail");
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário não autenticado tenta criar barbeiro")
        void deveLancarExcecaoQuandoNaoAutenticado() {
            when(repository.existsByRole(Role.ADMIN)).thenReturn(true);
            when(repository.existsByEmail(barbeiroRequest.email())).thenReturn(false);

            // sem contexto de autenticação
            SecurityContextHolder.clearContext();

            assertThatThrownBy(() -> barberService.register(barbeiroRequest))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("autenticado como ADMIN");
        }

        @Test
        @DisplayName("Deve lançar exceção quando usuário autenticado não é ADMIN")
        void deveLancarExcecaoQuandoNaoEAdmin() {
            when(repository.existsByRole(Role.ADMIN)).thenReturn(true);
            when(repository.existsByEmail(barbeiroRequest.email())).thenReturn(false);

            mockAuthenticatedWithRole("ROLE_CLIENT");

            assertThatThrownBy(() -> barberService.register(barbeiroRequest))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Apenas administradores podem cadastrar barbeiros");
        }

        @Test
        @DisplayName("Deve registrar barbeiro com sucesso quando ADMIN autenticado")
        void deveRegistrarBarbeiroComSucesso() {
            Barber barbeiro = new Barber();
            barbeiro.setId(UUID.randomUUID());
            barbeiro.setName("Pedro Barbeiro");
            barbeiro.setEmail("pedro@barbearia.com");
            barbeiro.setCpf("529.982.247-25");
            barbeiro.setPhone("11988888888");
            barbeiro.setRole(Role.BARBEIRO);

            when(repository.existsByRole(Role.ADMIN)).thenReturn(true);
            when(repository.existsByEmail(barbeiroRequest.email())).thenReturn(false);
            when(passwordEncoder.encode(barbeiroRequest.password())).thenReturn("senhaCriptografada");
            when(repository.save(any(Barber.class))).thenReturn(barbeiro);

            mockAuthenticatedAdmin();

            BarberResponse response = barberService.register(barbeiroRequest);

            assertThat(response).isNotNull();
            assertThat(response.name()).isEqualTo("Pedro Barbeiro");
            verify(repository).save(any(Barber.class));
        }
    }

    // =========================================================
    // fetch
    // =========================================================
    @Nested
    @DisplayName("fetch")
    class Fetch {

        @Test
        @DisplayName("Deve retornar barbeiro detalhado com sucesso")
        void deveRetornarBarbeiroDetalhado() {
            Address address = new Address("Rua A", "Bairro B", "100", "São Paulo", "SP");
            barber.setAddress(address);

            when(repository.findById(barberId)).thenReturn(Optional.of(barber));

            BarberDetailed result = barberService.fetch(barberId);

            assertThat(result).isNotNull();
            assertThat(result.name()).isEqualTo("João Admin");
            assertThat(result.email()).isEqualTo("admin@barbearia.com");
        }

        @Test
        @DisplayName("Deve lançar exceção quando barbeiro não encontrado")
        void deveLancarExcecaoQuandoBarbeiroNaoEncontrado() {
            when(repository.findById(barberId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> barberService.fetch(barberId))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Barbeiro não encontrado");
        }
    }

    // =========================================================
    // alter
    // =========================================================
    @Nested
    @DisplayName("alter")
    class Alter {

        @Test
        @DisplayName("Deve alterar barbeiro com sucesso")
        void deveAlterarBarbeiroComSucesso() {
            UpdateBarber updateBarber = new UpdateBarber(
                    "João Atualizado", "joao@novo.com", "529.982.247-25", "11977777777", null);

            when(repository.findById(barberId)).thenReturn(Optional.of(barber));

            barberService.alter(updateBarber, barberId);

            verify(repository).save(any(Barber.class));
        }

        @Test
        @DisplayName("Deve lançar exceção quando barbeiro não encontrado ao alterar")
        void deveLancarExcecaoQuandoBarbeiroNaoEncontradoAoAlterar() {
            UpdateBarber updateBarber = new UpdateBarber(
                    "João Atualizado", "joao@novo.com", "529.982.247-25", "11977777777", null);

            when(repository.findById(barberId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> barberService.alter(updateBarber, barberId))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Barbeiro não encontrado");
        }
    }

    // =========================================================
    // delete
    // =========================================================
    @Nested
    @DisplayName("delete")
    class Delete {

        @Test
        @DisplayName("Deve deletar barbeiro com sucesso")
        void deveDeletarBarbeiroComSucesso() {
            when(repository.findById(barberId)).thenReturn(Optional.of(barber));
            when(repository.getReferenceById(barberId)).thenReturn(barber);

            barberService.delete(barberId);

            verify(repository).delete(barber);
        }

        @Test
        @DisplayName("Deve lançar exceção quando barbeiro não encontrado ao deletar")
        void deveLancarExcecaoQuandoBarbeiroNaoEncontradoAoDeletar() {
            when(repository.findById(barberId)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> barberService.delete(barberId))
                    .isInstanceOf(BarberException.class)
                    .hasMessageContaining("Barbeiro não encontrado");
        }
    }

    // =========================================================
    // isBarber
    // =========================================================
    @Nested
    @DisplayName("isBarber")
    class IsBarber {

        @Test
        @DisplayName("Deve retornar true quando email pertence a um barbeiro")
        void deveRetornarTrueParaBarbeiro() {
            when(repository.findByEmail("admin@barbearia.com")).thenReturn(Optional.of(barber));

            assertThat(barberService.isBarber("admin@barbearia.com")).isTrue();
        }

        @Test
        @DisplayName("Deve retornar false quando email não pertence a nenhum barbeiro")
        void deveRetornarFalseParaNaoBarbeiro() {
            when(repository.findByEmail("cliente@email.com")).thenReturn(Optional.empty());

            assertThat(barberService.isBarber("cliente@email.com")).isFalse();
        }

        @Test
        @DisplayName("Deve retornar false quando email é nulo")
        void deveRetornarFalseParaEmailNulo() {
            assertThat(barberService.isBarber(null)).isFalse();
        }
    }

    // =========================================================
    // helpers para mock do SecurityContext
    // =========================================================

    private void mockAuthenticatedAdmin() {
        mockAuthenticatedWithRole("ROLE_ADMIN");
    }

    private void mockAuthenticatedWithRole(String role) {
        Authentication authentication = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);

        GrantedAuthority authority = new SimpleGrantedAuthority(role);
        doReturn(List.of(authority)).when(authentication).getAuthorities();
        when(authentication.isAuthenticated()).thenReturn(true);
        when(securityContext.getAuthentication()).thenReturn(authentication);

        SecurityContextHolder.setContext(securityContext);
    }
}
