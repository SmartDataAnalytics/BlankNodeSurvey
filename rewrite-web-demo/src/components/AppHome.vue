<template>
  <div class="container bg">
    <div class="row" style="justify-content:center;">
      <h1>SPARQL Bnode Web Demo</h1>
    </div>
    <div class="row" style="justify-content:center;">
      <h2>Bridging the muddy parts of your data</h2>
    </div>

    <div class="row">
      <div class="col-sm-3 offset-sm-1">
        <b-dropdown text="Endpoint ..." variant="primary">
          <b-dropdown-item v-for="option in availableEndpoints" :selected="option.url==endpointUrl" :key="option.url" @click="endpointUrl=option.url">{{option.label}}</b-dropdown-item>
        </b-dropdown>

        <div class="form-text"></div>

      </div>

      <div class="col-sm-7">
        <fieldset :disabled="taskDone">
      　　  <div class="form-group">
            <input class="form-control" v-bind:class="{ 'is-invalid': taskFail, 'is-valid': taskSuccess }" type="text" v-model="endpointUrl" @input="validate">
       　　 </div>
　　　　　　  </fieldset>
      </div>
    </div>

    <div class="row">
      <div class="col-sm-3 offset-sm-1">

        <b-dropdown text="Query ..." variant="primary">
          <b-dropdown-item v-for="option in availableQueries" :key="option.id" :selected="option.value==activeQuery" @click="activeQuery=inStmt=option.value">{{option.label}}</b-dropdown-item>
        </b-dropdown>
        <br />
		<p class="strokeme">Suggestions depend on selected endpoint</p>
      </div>

      <div class="col-sm-6">
        <codemirror v-model="inStmt" :options="cmOptions"></codemirror>
      </div>

      <div class="col-sm-1">
        <div class="form-text"></div>

        <button type="button" class="btn btn-success" @click="activeQuery=inStmt; runQuery();">Run</button>
      </div>
    </div>

    <div class="form-text"></div>


    <div class="row">

      <div class="col-sm-3 offset-sm-1">
        <select class="form-control" v-model="activeMode">
          <option v-for="option in availableModes" v-bind:value="option.id">
            {{ option.label }}
          </option>
        </select>
        <span>Selected: {{ activeMode }}</span>

<!--
        <b-dropdown id="dropdown-1" text="Mode..." variant="primary">
          <b-dropdown-item>Query rewrite</b-dropdown-item>
          <b-dropdown-item>Result set transform</b-dropdown-item>
        </b-dropdown>
-->
<!--
        <div class="form-check-inline">
          <label class="form-check-label">
            <input type="radio" class="form-check-input" name="optradio">Option 1
          </label>
        </div>
        <div class="form-check-inline">
          <label class="form-check-label">
            <input type="radio" class="form-check-input" name="optradio">Option 2
          </label>
        </div>
-->


      </div>
    </div>

    <div class="form-text"></div>

    <div class="row" v-show="activeMode == 1">

      <div class="col-sm-3 offset-sm-1" v-show="!taskDone">
        <b-dropdown text="Profiles ..." variant="primary">
          <b-dropdown-item v-if="availableProfiles.length==0" disabled>(empty)</b-dropdown-item>
          <b-dropdown-item v-for="option in availableProfiles" :selected="option.id==activeProfile" :key="option.id" @click="activeProfile=option">{{option.label}}</b-dropdown-item>
        </b-dropdown> ({{activeProfile.label || '(no profile set)'}})
      </div>

      <div class="col-sm-6">
        <codemirror v-model="outStmt" :options="cmOptions"></codemirror>
      </div>

      <div class="col-sm-1">
        <button type="button" class="btn btn-success" @click="activeQuery=outStmt; runQuery();">Run</button>
      </div>
    </div>



    <div class="form-text"></div>

    <div class="row">
      <div class="col">
        <hr class="mt-5 mb-5">
      </div>
    </div>

    <div class="row">

      <div class="col-sm-3 offset-sm-1">
        <b-dropdown text="History ..." variant="primary">
          <b-dropdown-item v-if="historyQueries.length==0" disabled>(empty)</b-dropdown-item>
          <b-dropdown-item v-for="option in historyQueries" :selected="option.value==activeQuery" :key="option.value" @click="activeQuery=option.value">{{option.label}}</b-dropdown-item>
        </b-dropdown>
      </div>

      <div class="col-sm-6">
        <codemirror v-model="activeQuery" :options="cmOptions"></codemirror>
      </div>

      <div class="col-sm-1">
        <div class="form-text"></div>

        <button type="button" class="btn btn-success" v-on:click="runQuery()">Run</button>
      </div>
    </div>


    <div class="row">
      <div class="col-sm-7 offset-sm-4">
        <table class="striped" hover>
          <tr>
            <th v-for="varName in activeResultSet.head.vars">{{varName}}</th>
          </tr>
          <tr v-for="binding in activeResultSet.results.bindings">
            <td v-for="varName in activeResultSet.head.vars">
              <a v-if="binding[varName].type=='uri'" :href="binding[varName].value" @click.prevent="inStmt=`SELECT * { <${binding[varName].value}> ?p ?o }`">{{binding[varName].value.split("/").pop()}}</a>
              <span v-if="binding[varName].type!='uri'">{{ binding[varName].value }}</span>
              <span v-if="binding[varName].datatype">binding[varName].datatype</span>
            </td>
          </tr>
		</table>
      </div>
    </div>


<!--
    <div class="row"　v-show="!taskDone">
      <div class="col-sm-6 offset-sm-3 text-center">
        <button type="button" class="btn btn-outline-danger btn-sm m-2" v-on:click='showSpoiler = !showSpoiler'>{{showSpoiler ? 'Hide' : 'Show'}} spoiler</button>
      </div>
    </div>


    <div class="row">
      <div class="col-sm-6 offset-sm-3 text-center">
        <fieldset :disabled="taskDone">
      　　  <div class="form-group">
            <input class="form-control form-control-lg" v-bind:class="{ 'is-invalid': taskFail, 'is-valid': taskSuccess }" type="text" v-model="answer" @input="validate">
       　　 </div>
　　　　　　</fieldset>
　　　　　　</div>
    </div>
-->
    <div class="form-text"></div>


  </div>
</template>

<script>

import 'codemirror/mode/sparql/sparql.js'
// theme css
import 'codemirror/theme/base16-dark.css'

const putItemsFirst = (data, item) => {
  const i = data.indexOf(item);
  if(i > 0) {
      data.splice(i, 1);
      data.unshift(item);
  }
  return data;
}; 

export default {
  name: 'AppHome',
  data() {
    let availableModes = [{id: 1, label: 'Query Rewrite'}, {id: 2, label: 'Resultset Transform'}];

    return {
      availableEndpoints: [{ id: 1, label: 'DBpedia', url: 'http://dbpedia.org/sparql' }],
      availableQueries: [
        { id: 1, value: 'SELECT * { ?s ?p ?o }', label: 'Everything' }
      ],
      historyQueries: [],
      availableProfiles: [],
      availableModes: availableModes,
      activeMode: 1,
      activeQuery: '',
      activeProfile: '',
      endpointUrl: '',
      outStmt: '',
      taskData:{},
      taskSuccess: false,
      taskDone: false,
      taskFail: false,
      showSpoiler: false,
      answer: '',
      activeResultSet: {head: {vars: []}, results: []},
      cmOptions: {
        tabSize: 4,
        mode: 'application/sparql-query',
        //theme: 'base16-dark',
        lineNumbers: true,
        line: true
      },
      inStmt: 'SELECT * { ?s ?p ?o }'
    };
  },
  watch: {
    inStmt(val, oldVal) { this.updateRewrite(); },
    activeProfile(val, oldVal) { this.updateRewrite(); },
    'taskDone'(v) {
      this.showSpoiler = v;
    }
  },
  created() {
    this.loadProfiles();
  },
  methods: {
    updateRewrite() {
      axios.get('http://localhost:7531/rewrite', {
        params: { profile: this.activeProfile.label, query: this.inStmt }
      })
      .then(response => this.outStmt = response.data);

    },
    loadProfiles() {
      axios.get('http://localhost:7531/profiles')
        .then(response => this.availableProfiles = response.data.map(x => ({id: x, label: x.split("/").pop()})));
    },

    runQuery() {
      let maxHistoryQueries = 5;
      let idx = this.historyQueries.findIndex(x => x.value == this.activeQuery);
      if(idx >= 0) {
        if(idx > 0) {
          const item = this.historyQueries[idx];
          this.historyQueries.splice(idx, 1);
          this.historyQueries.unshift(item);
        }
      } else {
        const tmp = this.availableQueries.find(x => x.value == this.activeQuery) || {label: this.activeQuery};
        this.historyQueries.unshift({value: this.activeQuery, label: tmp.label });
      }
      this.historyQueries.splice(maxHistoryQueries);


      let self = this;
      axios.get(this.endpointUrl, {
        params: { query: this.activeQuery }
      })
      .then(response => this.activeResultSet = response.data)
      .catch(function (error) {
        alert(JSON.stringify(error));
      })
    },

    newTask() {
/*      do {
        this.newTaskCore();
      } while(this.taskDone); */
    },

    // Sometimes it happens that a task is immediately solved, in that case just generate a new one
    newTaskCore() {
      this.taskData = {}; //pickTask();
      this.taskDone = false;
      this.taskFail = false;
      this.taskSuccess = false;
      this.answer = this.taskData.srcWordKanjiLbl;
      this.validate();
    },

    giveUp() {
    },

    validate() {
     if(this.answer == this.taskData.tgtWordLbl) {// || this.answer == 'test') {
       this.taskDone = true;
       this.taskSuccess = true;
     }
    }
  }
};

let data={};
import axios from 'axios';

function pickTask() {
  return {};
  let n = data.length;

  // Derive schema from first row and remove unwanted keys (somewhat hacky)
  let tmp = data[0];
  let reject = {'id': true, 'id_type': true, 'german': true, 'hiragana': true };
  let schema = Object.getOwnPropertyNames(tmp).filter(x => !reject[x]);

  let m = schema.length;

  // Pick an entry from the table
  let rowIdx = Math.floor((Math.random() * n));
  let row = data[rowIdx];

  // Pick a source
  let srcColIdx = Math.floor((Math.random() * m));
  let tgtColIdx;
  do {
    tgtColIdx = Math.floor((Math.random() * m));
  } while(srcColIdx == tgtColIdx)

  let srcCol = schema[srcColIdx];
  let tgtCol = schema[tgtColIdx];

  console.log('tgtCol', tgtCol, tgtColIdx, m, schema);
 
  let result = {
    srcWordKanjiLbl: row[srcCol][0],
    srcWordHiraganaLbl: row['hiragana'][0],
    srcWordRentaikeiLbl: row['rentaikei'][0],
    srcWordFormLbl: srcCol,
    srcWordTrnsLbl: row['german'][0],
    tgtWordLbl: row[tgtCol][0],
    tgtWordFormLbl: tgtCol
  };

  console.log("Task", result);
  return result;
}




</script>

<style scoped>
    h1  {
        color: white;
        justify-content:center;
    }


.strokeme {
  line-height: 1;
  font-size: 10px;
/*  color: white;
  text-shadow: -1px -1px 0 #000, 1px -1px 0 #000, -1px 1px 0 #000, 1px 1px 0 #000;
*/
}

</style>

<style>
    .CodeMirror {
      font-family: Arial, monospace;
      font-size: 10px;
      height: auto !important;
      min-height: 5em;
    }

    html {
      width: 100%;
      height: 100%;
    }

    body {
      /* background: url("Clinton-Township.1.jpg") no-repeat center center fixed; */
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }
</style>
