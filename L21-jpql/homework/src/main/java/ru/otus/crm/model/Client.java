package ru.otus.crm.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "client")
public class Client implements Cloneable {

    @Id
    @SequenceGenerator(name = "client_gen", sequenceName = "client_seq",
        initialValue = 1, allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "client_gen")
//    @Column(name = "client_id")
    private Long id;

    private String name;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "address_id")
    private Address address;

    @OneToMany(orphanRemoval = true, mappedBy = "client", cascade = CascadeType.ALL)
    private List<Phone> phones;

    public Client(String name) {
        this.id = null;
        this.name = name;
    }

    public Client(
        Long id,
        String name
    ) {
        this.id = id;
        this.name = name;
    }

    public Client(
        Long id,
        String name,
        Address address,
        List<Phone> phones
    ) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phones = new ArrayList<>();
        phones.forEach(phone -> this.phones.add(new Phone(phone.getId(), phone.getNumber(), this)));
    }

    @Override
    public Client clone() {
        return new Client(this.id, this.name, this.address.clone(), List.copyOf(this.phones));
    }

    @Override
    public String toString() {
        return "Client{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", address='" + address + '\'' +
            ", phones='" + phones + '\'' +
            '}';
    }
}
