package database;

import java.util.Date;
import javax.annotation.Generated;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="EclipseLink-2.5.2.v20140319-rNA", date="2019-02-13T00:31:44")
@StaticMetamodel(AlarmEntity.class)
public class AlarmEntity_ { 

    public static volatile SingularAttribute<AlarmEntity, String> nazivZvona;
    public static volatile SingularAttribute<AlarmEntity, Date> vreme;
    public static volatile SingularAttribute<AlarmEntity, Integer> periodZvonjave;
    public static volatile SingularAttribute<AlarmEntity, Integer> id;
    public static volatile SingularAttribute<AlarmEntity, String> nazivAlarma;
    public static volatile SingularAttribute<AlarmEntity, Integer> onoff;

}