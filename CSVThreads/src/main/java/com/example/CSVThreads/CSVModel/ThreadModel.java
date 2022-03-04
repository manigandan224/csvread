package com.example.CSVThreads.CSVModel;

import com.sun.istack.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Data
@EqualsAndHashCode
@Entity
public class ThreadModel implements Serializable {
    private static final long serialVersionUID=1L;
    @Id
    @Column(name="ID",nullable=false)
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private long id;
    @NotNull
    @Column(nullable = false)
    private String Name;
    @NotNull
    @Column(nullable=false)
    private String Address;
    @NotNull
    @Column(nullable = false)
    private String Email;
    @NotNull
    @Column(nullable=false)
    private Date time;
    public static String[] getFields(){
        return new String[]{"Id","Name","Address","Email"};
    }
}
