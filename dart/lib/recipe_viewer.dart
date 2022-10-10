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
          textTheme: Theme.of(context).textTheme.apply(
              fontFamily: 'Arial',
              fontSizeFactor: 1.3,
              bodyColor: Colors.black,
              displayColor: Colors.white)),
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
  Json jsonParser = Json();

  void visualizeJson(String json) {
    setState(() {
      branches = jsonParser
          .parseJsonString(jsonTest); //TODO: remove "Test" after testing
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
        children: generateBranchVisualization(context),
      ),
    );
  }

  List<Widget> generateBranchVisualization(BuildContext context) {
    List<Widget> widgets = List.empty(growable: true);
    final root = jsonParser.getResultRoot();
    if (root != null) {
      List<Branch> currentLevel = <Branch>[root];
      do {
        widgets.insert(
            0,
            Row(
                children: currentLevel
                    .map((branch) => branch.graph(context))
                    .toList()));
        currentLevel = returnPredecessors(currentLevel);
      } while (currentLevel.isNotEmpty);
    }
    return widgets;
  }

  List<Branch> returnPredecessors(List<Branch> parent) {
    List<Branch> pre = List.empty(growable: true);
    for (Branch b in parent) {
      pre.addAll(b.prev);
    }
    return pre;
  }
}
