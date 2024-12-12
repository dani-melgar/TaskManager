package model.exporter;

import com.google.gson.*;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Adaptador de Gson para serializar y deserializar objetos {@link LocalDate}.
 * <p>
 * Esta clase permite manejar tareas que utilizan {@link LocalDate} en lugar del
 * obsoleto {@link Date}, solucionando errores derivados del cambio de tipo de dato
 * en la clase {@link Task}, dicho atributo en {@code Task} es 
 * {@code private LocalDate date}.
 * </p>
 * <p>
 * Se utiliza el formato ISO-8601 (por ejemplo, "2024-12-06") para la conversion
 * de objetos {@link LocalDate} a cadenas y viceversa.
 * </p>
 * <p>
 * Agradecimientos especiales a ChatGPT, 
 * <a href="https://stackoverflow.com/">Stack Overflow</a> y 
 * la documentacion oficial de Java ({@link java.util.Date}, {@link java.time.LocalDate})
 * por su ayuda en esta clase.
 * </p>
 */

public class LocalDateAdapter implements JsonSerializer<LocalDate>, JsonDeserializer<LocalDate> {
	/**
	 * Formato utilizado para serializar y deserializar fechas.
	 */
	private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

	/**
	 * Serializa un objeto {@link LocalDate} en formato ISO-8601.
	 *
	 * @param src       El objeto {@code LocalDate} a serializar.
	 * @param typeOfSrc El tipo de origen (se ignora en este caso).
	 * @param context   El contexto de serializacion de Gson.
	 * @return Un objeto {@link JsonPrimitive} que contiene la fecha como cadena.
	 */
	@Override
	public JsonElement serialize(LocalDate src, Type typeOfSrc, JsonSerializationContext context) {
		return new JsonPrimitive(src.format(formatter));
	}

	/**
	 * Deserializa un {@link JsonElement} en un objeto {@link LocalDate}.
	 *
	 * @param json   El elemento JSON que contiene la fecha como cadena.
	 * @param typeOfT El tipo esperado de destino (se ignora en este caso).
	 * @param context El contexto de deserializacion de Gson.
	 * @return El objeto {@code LocalDate} deserializado.
	 * @throws JsonParseException Si el formato de la fecha no es correcto.
	 */
	@Override
	public LocalDate deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
		return LocalDate.parse(json.getAsString(), formatter);
	}
}