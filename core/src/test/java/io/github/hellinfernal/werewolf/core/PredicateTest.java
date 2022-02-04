package io.github.hellinfernal.werewolf.core;

import java.util.function.Predicate;

import org.junit.jupiter.api.Test;


public class PredicateTest {

   @Test
   void testPredicate() {
      final String text = "This is a text";
      if (text == null || text.isEmpty()) {
         System.out.println("text is missing");
      } else {
         System.out.println("text is present!");
      }

      final Predicate<String> condition = new IsNull().or(s -> s.isEmpty());

      if ( condition.test(text)) {
         System.out.println("text is missing");
      } else {
         System.out.println("text is present!");
      }
   }

   public boolean testIsNull(final String input) {
      return input == null;
   }

   public final class IsNull implements Predicate<String> {

      @Override
      public boolean test( final String input ) {
         return input == null;
      }
   }

   public final class IsEmpty implements Predicate<String> {

      @Override
      public boolean test( final String input ) {
         return input.isEmpty();
      }
   }
}
