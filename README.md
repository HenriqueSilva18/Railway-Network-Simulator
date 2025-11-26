
<h1>Railway Network Simulator</h1>


<br />
<h3>My Role 👨‍💻</h3>
<p><strong>Responsible for software design/dev components, statistical analysis, and project management.</strong></p>

<h2>Description</h2>
This project is a comprehensive Railway Network Simulator developed in Java, designed to manage complex railway topologies, optimize routes, and simulate train logistics. Developed within the scope of the Informatics Engineering degree at ISEP, the system allows 'administrators' to define network topologies (stations, lines, connections) and manage resources. It enables 'players' to operate trains, manage cargo, and execute simulation scenarios based on real-world constraints. The application features both a Console UI and a Graphical User Interface (JavaFX).
<br />

<h2>Technologies Used</h2>
<ul>
<li>Programming Language: Java</li>
<li>Build Tool: Maven</li>
<li>GUI Framework: JavaFX (`.fxml` files)</li>
<li>Testing: JUnit & Jacoco</li>
<li>Algorithms: Dijkstra, Fleury, and Matrix manipulation for graph analysis</li>
<li>Data Storage: CSV and Text file manipulation for persistence</li>
</ul>


<h2>Features</h2>
<ul>
<li><strong>Network Topology Management</strong>: Create maps, build railway lines, add cities, and manage station upgrades.</li>
<li><strong>Resource Management</strong>: Purchase and manage locomotives, carriages, and organize trains.</li>
<li><strong>Simulation Engine</strong>: Execute simulations of train movements, schedule management, and cargo transport.</li>
<li><strong>Graph Theory Algorithms</strong>: Calculate shortest paths, minimum spanning trees, and analyze network connectivity (e.g., identifying articulation points).</li>
<li><strong>Dual User Interfaces</strong>: Full support for both a text-based Console UI and a visual GUI for map rendering.</li>
</ul>


<h2>Project Structure</h2>
<ul>
<li><code>src/main/java/.../Main.java</code> & <code>MainApp.java</code>: The entry points for the Console and JavaFX applications respectively.</li>
<li><code>domain/</code>: Core business logic classes including <code>Station</code>, <code>RailwayLine</code>, <code>Train</code>, and <code>Map</code>.</li>
<li><code>controller/</code>: Orchestrates logic between the UI and the Domain (e.g., <code>CreateMapController</code>, <code>SimulatorController</code>).</li>
<li><code>mdisc/</code>: Contains specific algorithms for discrete mathematics tasks like <code>DijkstraAlgorithm</code> and <code>FleuryAlgorithm</code>.</li>
<li><code>repository/</code>: Handles data persistence and in-memory storage for objects like <code>MapRepository</code> and <code>TrainRepository</code>.</li>
<li><code>ui/gui/</code> & <code>ui/console/</code>: Handles user interactions, including the rendering of map visualizations and dialog menus.</li>
</ul>

Responsible for:
