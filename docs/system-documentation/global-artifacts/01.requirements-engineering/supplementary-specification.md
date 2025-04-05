# Supplementary Specification (FURPS+)

## Functionality

_Specifies functionalities that:  
&nbsp; &nbsp; (i) are common across several US/UC;  
&nbsp; &nbsp; (ii) are not related to US/UC, namely: Audit, Reporting and Security._

* All those who wish to use the application must be authenticated with 
a password of seven alphanumeric characters, including three capital 
letters and two digits.

* Business rules validation must be respected when recording and updating data.
* Adopt best practices for identifying requirements, and for OO software analysis
and design.

## Usability

_Evaluates the user interface. It has several subcategories,
among them: error prevention; interface aesthetics and design; help and
documentation; consistency and standards._

 * The application documentation must be in English.
 * Javadoc must be used to generate useful documentation for Java code.
 * All the images/figures produced during the software development process should be recorded in SVG format.

## Reliability

_Refers to the integrity, compliance and interoperability of the software. The requirements to be considered are: frequency and severity of failure, possibility of recovery, possibility of prediction, accuracy, average time between failures._

* The application ought to employ object serialization to guarantee the
  persistence of the data in two successive runs.

## Performance

_Evaluates the performance requirements of the software, namely: response time, start-up time, recovery time, memory consumption, CPU usage, load capacity and application availability._

* n/a

## Supportability

_The supportability requirements gathers several characteristics, such as:
testability, adaptability, maintainability, compatibility,
configurability, installability, scalability and more._

* The class structure must be designed to allow easy maintenance and the addition of new features following the best Object-Oriented (OO) practices.
* The development team must implement unit tests for all methods, except for the methods that implement Input/Output operations.
* The application must follow an iterative and incremental development process using the agile SCRUM approach.
* The implementation must follow a Test-Driven Development (TDD) approach to increase solution maintainability.
* The JaCoCo plugin should be used to generate the coverage report.
* The app needs to support English language.
* The application ought to employ object serialization to guarantee the
  persistence of the data in two successive runs.

## +

### Design Constraints

_Specifies or constraints the system design process. Examples may include: programming languages, software process, mandatory standards/patterns, use of development tools, class library, etc._

* The application must be developed in Java language.
* The Unit tests should be implemented using the JUnit 5 framework.
* The JaCoCo plugin should be used to generate the coverage report.

### Implementation Constraints

_Specifies or constraints the code or construction of a system such
as: mandatory standards/patterns, implementation languages,
database integrity, resource limits, operating system._

*  The app must support English language.
*  The development team must implement unit tests for all methods, except for methods that implement Input/Output operations.
*  The unit tests should be implemented using the JUnit 5 framework.
*  The JaCoCo plugin will generate the coverage report.
*  The team must adopt recognized coding standards (e.g., CamelCase);

### Interface Constraints

_Specifies or constraints the features inherent to the interaction of the
system being developed with other external systems._

* n/a

### Physical Constraints

_Specifies a limitation or physical requirement regarding the hardware used to house the system, as for example: material, shape, size or weight._

* n/a