package matching;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.example.richfieldpantryapp.matching.IngredientNormalizer;
import com.example.richfieldpantryapp.matching.UnitConverter;

import org.junit.Test;

public class IngredientNormalizerTest {

    @Test
    public  void lowerCasesAndTrims() {
        assertEquals("tomato", IngredientNormalizer.normalize("  TOMATO  "));
    }

    @Test
    public void handlesRegularAndIrregularPlurals() {
        assertEquals("tomato", IngredientNormalizer.normalize("tomatoes"));
        assertEquals("egg", IngredientNormalizer.normalize("Eggs"));
        assertEquals("potato", IngredientNormalizer.normalize("Potatoes"));
        assertEquals("berry", IngredientNormalizer.normalize("berries"));
        assertEquals("chilli", IngredientNormalizer.normalize("Chillies"));
        assertEquals("bay leaf", IngredientNormalizer.normalize("bay leaves"));
        assertEquals("peach", IngredientNormalizer.normalize("peaches"));
//        assertEquals("clove", IngredientNormalizer.singularize("cloves"));
    }

    @Test
    public void leavesFalsePluralsAlone() {
        assertEquals("hummus", IngredientNormalizer.normalize("hummus"));
        assertEquals("couscous", IngredientNormalizer.normalize("Couscous"));
        assertEquals("asparagus", IngredientNormalizer.normalize("asparagus"));
        assertEquals("oats", IngredientNormalizer.normalize("Rolled Oats"));
    }

    @Test
    public void onlyTheLastWordIsSingularised() {
        assertEquals("cherry tomato", IngredientNormalizer.normalize("cherry tomatoes"));
        assertEquals("spring onion", IngredientNormalizer.normalize("Spring Onions"));
    }

    @Test
    public void dropsDescriptorsAndBrackets() {
        assertEquals("tomato", IngredientNormalizer.normalize("Fresh chopped tomatoes (ripe)"));
        assertEquals("egg", IngredientNormalizer.normalize("free-range eggs"));
        assertEquals("milk", IngredientNormalizer.normalize("Full cream milk"));
    }

    @Test
    public void mapsSynonymsToOneCanonicalName() {
        assertEquals("coriander", IngredientNormalizer.normalize("cilantro"));
        assertEquals("spring onion", IngredientNormalizer.normalize("scallions"));
        assertEquals("beef mince", IngredientNormalizer.normalize("Ground Beef"));
        assertEquals("chicken", IngredientNormalizer.normalize("chicken breasts"));
        assertEquals("cheddar cheese", IngredientNormalizer.normalize("Cheddar"));
        assertEquals("bell pepper", IngredientNormalizer.normalize("green peppers"));
        assertTrue(IngredientNormalizer.sameIngredient("Tomatoes", "tomato"));
    }

    @Test
    public void unitParsingIsForgiving() {
        assertEquals("g", UnitConverter.parse("Grams").canonical);
        assertEquals("kg", UnitConverter.parse("kilos").canonical);
        assertEquals("tbsp", UnitConverter.parse("Tablespoons").canonical);
        assertEquals("can", UnitConverter.parse("tins").canonical);
        assertEquals("pcs", UnitConverter.parse("").canonical);
        assertEquals("pcs", UnitConverter.parse("pieces").canonical);
        assertEquals(UnitConverter.Family.UNKNOWN, UnitConverter.parse("jars").family);
        assertEquals("jar", UnitConverter.parse("jars").canonical);
        assertEquals(1000.0, UnitConverter.parse("kg").toBase(1), 1e-9);
        assertEquals(1000.0, UnitConverter.parse("l").toBase(1), 1e-9);
    }
}
