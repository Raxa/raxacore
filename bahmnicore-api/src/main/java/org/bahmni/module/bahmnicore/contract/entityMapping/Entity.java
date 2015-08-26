package org.bahmni.module.bahmnicore.contract.entityMapping;

import lombok.Data;

import java.util.List;

@Data
public class Entity <T1,T2> {
    public T1 entity;

    public List<T2> mappings;

    public Entity () {
    }

    public Entity(T1 entity, List<T2> mappings) {
        this.entity = entity;
        this.mappings = mappings;
    }
}
