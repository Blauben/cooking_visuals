import 'package:flutter/material.dart';

import 'branch.dart';
import 'json.dart';

const String jsonTest = """
[{
"id":1,
"environment":"Brett",
"nextKey":3,
"instructions": [
{
"type":"Ingredient",
"name":"Zwiebel",
"details": ["Eine Ganze"]
},
{
"type":"Instruction",
"name":"schneiden",
"details": ["in Scheiben"]
}]
},
{
"id":2,
"environment":"Schüssel",
"nextKey":3,
"instructions": []
},
{
"id":3,
"environment":"Schüssel",
"nextKey":0,
"instructions": [
{
"type":"Instruction",
"name":"zugeben",
"details": []
}]
}]
""";

void main() {
  runApp(const Viewer());
}

class Viewer extends StatelessWidget {
  const Viewer({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Graph Viewer',
      theme: ThemeData(
        primarySwatch: Colors.blue,
      ),
      home: const Graph(title: 'Graph'),
    );
  }
}

class Graph extends StatefulWidget {
  const Graph({super.key, required this.title});

  final String title;

  @override
  State<StatefulWidget> createState() => GraphData();
}

class GraphData extends State<Graph> {
  List<Branch> branches = List.empty();

  void visualizeJson(String json) {
    setState(() {
      branches =
          Json.parseJsonString(jsonTest); //TODO: remove "Test" after testing
    });
  }

  @override
  Widget build(BuildContext context) {
    visualizeJson("");
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.title),
      ),
      body: Column(
        children: generateBranchVisualization(),
      ),
    );
  }

  List<Widget> generateBranchVisualization() {
    List<Widget> widgets = List.empty(growable: true);
    for (Branch b in branches) {
      widgets.add(b.graph());
    }
    return widgets;
  }
}
