package com.pruebatecnica.microservicio_polizas.dominio.enums;

/**
 * Clase contenedora de los enumerados del dominio del microservicio de pólizas.
 * <p>
 * Agrupa los distintos enumerados utilizados para tipificar pólizas, parentescos
 * y coberturas de salud, garantizando consistencia entre la capa de dominio y la
 * base de datos.
 * </p>
 */
public final class Enums {

    /**
     * Constructor privado para evitar la instanciación de esta clase utilitaria.
     */
    private Enums() {
    }

    /**
     * Enumerado que representa los tipos de póliza disponibles en el sistema.
     * <p>
     * Cada constante está asociada a un identificador numérico que coincide con
     * el valor almacenado en la tabla {@code TiposPoliza} de la base de datos.
     * </p>
     */
    public enum TipoPoliza {
        /** Póliza de tipo vida con identificador 1. */
        VIDA(1),
        /** Póliza de tipo vehículo con identificador 2. */
        VEHICULO(2),
        /** Póliza de tipo salud con identificador 3. */
        SALUD(3);

        /** Valor numérico asociado al tipo de póliza en la base de datos. */
        private final int valor;

        /**
         * Constructor del enumerado.
         *
         * @param valor identificador numérico del tipo de póliza
         */
        TipoPoliza(int valor) {
            this.valor = valor;
        }

        /**
         * Retorna el valor numérico del tipo de póliza.
         *
         * @return identificador numérico
         */
        public int getValor() {
            return valor;
        }

        /**
         * Obtiene el {@link TipoPoliza} correspondiente a un identificador numérico.
         *
         * @param id identificador numérico a buscar
         * @return la constante {@link TipoPoliza} cuyo valor coincide con {@code id}
         * @throws IllegalArgumentException si no existe ninguna constante con ese identificador
         */
        public static TipoPoliza fromId(int id) {
            for (TipoPoliza t : values()) {
                if (t.valor == id) {
                    return t;
                }
            }
            throw new IllegalArgumentException("TipoPoliza inválido: " + id);
        }
    }

    /**
     * Enumerado que representa los tipos de parentesco utilizados para
     * clasificar a los beneficiarios de las pólizas.
     */
    public enum TipoParentesco {

        /** Parentesco padre con identificador 1. */
        PADRE(1),
        /** Parentesco madre con identificador 2. */
        MADRE(2),
        /** Parentesco cónyuge con identificador 3. */
        CONYUGE(3),
        /** Parentesco hijo con identificador 4. */
        HIJO(4);

        /** Identificador numérico del tipo de parentesco en la base de datos. */
        private final Integer id;

        /**
         * Constructor del enumerado.
         *
         * @param id identificador numérico del tipo de parentesco
         */
        TipoParentesco(Integer id) {
            this.id = id;
        }

        /**
         * Retorna el identificador numérico del tipo de parentesco.
         *
         * @return identificador numérico
         */
        public Integer getId() {
            return id;
        }

        /**
         * Obtiene el {@link TipoParentesco} correspondiente a un identificador numérico.
         *
         * @param id identificador numérico a buscar
         * @return la constante {@link TipoParentesco} cuyo identificador coincide con {@code id}
         * @throws IllegalArgumentException si no existe ninguna constante con ese identificador
         */
        public static TipoParentesco fromId(Integer id) {
            for (TipoParentesco t : values()) {
                if (t.id.equals(id)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("TipoParentesco inválido: " + id);
        }

        /**
         * Indica si el tipo de parentesco corresponde a padre o madre.
         *
         * @return {@code true} si es {@link #PADRE} o {@link #MADRE}; {@code false} en caso contrario
         */
        public boolean esPadreOMadre() {
            return this == PADRE || this == MADRE;
        }

        /**
         * Indica si el tipo de parentesco corresponde a cónyuge o hijo.
         *
         * @return {@code true} si es {@link #CONYUGE} o {@link #HIJO}; {@code false} en caso contrario
         */
        public boolean esConyugeOHijo() {
            return this == CONYUGE || this == HIJO;
        }
    }

    /**
     * Enumerado que representa los tipos de cobertura disponibles
     * para las pólizas de salud.
     */
    public enum TipoCoberturaSalud {

        /** Cobertura exclusiva para el titular de la póliza, identificador 1. */
        SOLO_CLIENTE(1),
        /** Cobertura para el titular y sus padres, identificador 2. */
        CLIENTE_PADRES(2),
        /** Cobertura para el titular, su cónyuge e hijos, identificador 3. */
        CLIENTE_CONYUGE_HIJOS(3);

        /** Identificador numérico de la cobertura en la base de datos. */
        private final Integer id;

        /**
         * Constructor del enumerado.
         *
         * @param id identificador numérico de la cobertura de salud
         */
        TipoCoberturaSalud(Integer id) {
            this.id = id;
        }

        /**
         * Retorna el identificador numérico del tipo de cobertura de salud.
         *
         * @return identificador numérico
         */
        public Integer getId() {
            return id;
        }

        /**
         * Obtiene el {@link TipoCoberturaSalud} correspondiente a un identificador numérico.
         *
         * @param id identificador numérico a buscar
         * @return la constante {@link TipoCoberturaSalud} cuyo identificador coincide con {@code id}
         * @throws IllegalArgumentException si no existe ninguna constante con ese identificador
         */
        public static TipoCoberturaSalud fromId(Integer id) {
            for (TipoCoberturaSalud t : values()) {
                if (t.id.equals(id)) {
                    return t;
                }
            }
            throw new IllegalArgumentException("TipoCobertura inválido: " + id);
        }
    }
}