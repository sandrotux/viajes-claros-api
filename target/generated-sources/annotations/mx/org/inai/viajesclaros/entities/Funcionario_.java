package mx.org.inai.viajesclaros.entities;

import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value = "org.hibernate.jpamodelgen.JPAMetaModelEntityProcessor")
@StaticMetamodel(Funcionario.class)
public abstract class Funcionario_ {

	public static volatile SingularAttribute<Funcionario, String> apellido2;
	public static volatile SingularAttribute<Funcionario, String> nombre;
	public static volatile SingularAttribute<Funcionario, Integer> id;
	public static volatile SingularAttribute<Funcionario, Integer> idDependencia;
	public static volatile SingularAttribute<Funcionario, String> email;
	public static volatile SingularAttribute<Funcionario, String> apellido1;
	public static volatile SingularAttribute<Funcionario, CatDependencia> catDependencia;

}

