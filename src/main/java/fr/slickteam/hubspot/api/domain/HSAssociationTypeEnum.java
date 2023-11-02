package fr.slickteam.hubspot.api.domain;

public enum HSAssociationTypeEnum {
   PARENT(14, "Parent Company"),
   CHILD(13, "Child Company");

   public final int id;
   public final String label;

   HSAssociationTypeEnum (int id, String label) {
      this.id = id;
      this.label = label;
   }
}
