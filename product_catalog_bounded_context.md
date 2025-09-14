# Product Catalog Bounded Context

## 1. Core Responsibility and Strategic Purpose

The Product Catalog is a core business domain responsible for acting as the definitive, canonical source of truth for all intrinsic product information. Its primary strategic purpose is to model what a product is in its purest form, completely decoupled from the concerns of how it is sold, priced, or stocked. This context ensures that all other parts of the business operate from a single, consistent, and validated set of product data.

It is the master repository for a product's physical characteristics, compliance details, and descriptive attributes. By centralizing this information, it eliminates data duplication and ambiguity, providing a stable foundation upon which other business domains, such as Inventory, Pricing, and Order Fulfillment, can be built.

## 2. Ubiquitous Language

To ensure clarity and prevent misinterpretation, the Product Catalog context establishes a strict and shared vocabulary (Ubiquitous Language) for all its concepts:

### Core Concepts

**Product**: The central Aggregate Root of this context. It represents a unique, sellable item identified by a SKU. It is not just a data container; it is an object that enforces its own consistency and business rules. All interactions with product data must go through the Product aggregate.

**SKU (Stock Keeping Unit)**: The unique, immutable identifier for a Product. It serves as the primary key for the aggregate.

### Value Objects

**Dimensions**: A Value Object that encapsulates the physical measurements of a Product. It has no identity of its own and is wholly owned by the Product. It is defined by its constituent parts.

- **Item Dimensions**: A specific set of dimensions describing the product itself, unboxed. This is critical for warehouse slotting and customer-facing specifications.
- **Package Dimensions**: A specific set of dimensions describing the product in its final, shippable packaging. This is the authoritative data for all logistics and fulfillment calculations.

**Attributes**: A Value Object that groups together additional, non-dimensional characteristics of a Product. This is an extensible container for properties like compliance information.

**HazmatInfo (Hazardous Material Information)**: A specific Value Object within Attributes that captures all necessary data for hazardous material compliance, such as its status and UN number.

**Value Object**: A concept representing a descriptive aspect of the domain with no conceptual identity. Dimensions and Attributes are value objects because we care about what they are, not who they are. Two Dimension objects with the same measurements are considered equal.

## 3. Boundaries and Exclusions

Defining what this context is not responsible for is as critical as defining what it is. The boundaries are clear and enforced to maintain high cohesion and low coupling with other domains.

### The Product Catalog context is NOT responsible for:

- **Inventory Levels**: The quantity on hand for a given SKU is a volatile, operational concern that belongs in an Inventory Management Bounded Context.

- **Pricing and Offers**: The price, sale price, or any promotional data is managed by a separate Pricing Bounded Context.

- **Supplier Information**: Details about who supplies a product, lead times, and purchase costs belong in a Supplier or Procurement Bounded Context.

- **Sales Orders**: The process of selling a product to a customer is handled by an Order Management Bounded Context.

- **Marketplace Listings**: The specific representation of a product on a sales channel (like an Amazon listing) is a separate concern. While our context is informed by Amazon's data model, it unifies this information into a canonical internal model. A dedicated Listings Management Bounded Context would handle the synchronization to and from external APIs.

## 4. Invariants (Business Rules)

The Product aggregate is responsible for enforcing its own consistency by protecting its invariants—rules that must be true at all times.

- A Product must always have a unique and non-empty SKU.
- A Product must always have a Title.
- Any DimensionMeasurement or WeightMeasurement must contain both a value and a unit. A value without a unit is meaningless and invalid within this context.
- The item dimensions must not be larger than the package dimensions.
- If the isHazmat flag in HazmatInfo is set to true, the unNumber attribute becomes mandatory. The aggregate will reject any attempt to create this invalid state.

## 5. Relationships with Other Bounded Contexts

The Product Catalog serves other contexts by providing them with the stable data they need to perform their functions. It communicates with them via well-defined APIs, often in a query-only pattern.

### Context Relationships

**Inventory Management Context**: References a Product by its SKU to track stock levels. It does not care about the product's dimensions, only its identifier.

**Pricing Context**: Associates a price with a SKU. It queries the Product Catalog for product titles to display alongside prices but does not modify product data.

**Order Management Context**: When an order is placed, this context queries the Product Catalog for the Product's Package Dimensions and weight to calculate shipping costs and select the appropriate packaging.

**Data Synchronization Context**: This is an upstream context responsible for populating the Product Catalog. It would contain the complex logic for calling external APIs (like the Amazon Catalog Items and Listings Items APIs), reconciling the data from both sources, and then using the Product Catalog's API to create or update the canonical Product record.