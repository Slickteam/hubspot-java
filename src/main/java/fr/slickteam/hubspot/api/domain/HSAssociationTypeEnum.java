package fr.slickteam.hubspot.api.domain;

/**
 * The enum Hs association type enum.
 */
public enum HSAssociationTypeEnum {
   /**
    * The Parent.
    */
   PARENT(14, "Parent Company"),
   /**
    * The Child.
    */
   CHILD(13, "Child Company");

   /**
    * The Id.
    */
   public final int id;
   /**
    * The Label.
    */
   public final String label;

   HSAssociationTypeEnum (int id, String label) {
      this.id = id;
      this.label = label;
   }
}
