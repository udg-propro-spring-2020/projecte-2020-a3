/// @author Miquel de Domingo i Giralt
/// @file JSON.java
/// @class JSON
/// @brief Interface for all classe which have to be converted to JSON
public interface JSON {
    /// @brief Parses the object properties to JSON style
    /// @pre ---
    /// @post Returns a tabbed string containing the object in JSON format
    public String toJSON();
}