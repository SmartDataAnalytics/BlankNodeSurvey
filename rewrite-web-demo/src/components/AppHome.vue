<template>
  <div class="container bg">
    <div class="row" style="justify-content:center;">
      <h1>SPARQL Bnode Web Demo</h1>
    </div>
    <div class="row">
      <div class="col-sm-3 offset-sm-3">
        <b-dropdown id="dropdown-1" text="Endpoint ..." class="m-md-2">
          <b-dropdown-item>First Action</b-dropdown-item>
          <b-dropdown-item>Second Action</b-dropdown-item>
          <b-dropdown-item>Third Action</b-dropdown-item>
          <b-dropdown-divider></b-dropdown-divider>
          <b-dropdown-item active>Active action</b-dropdown-item>
          <b-dropdown-item disabled>Disabled action</b-dropdown-item>
        </b-dropdown>
      </div>

      <div class="col-sm-3 offset-sm-6">
        <b-dropdown id="dropdown-1" text="Example Queries ..." class="m-md-2">
          <b-dropdown-item>First Action</b-dropdown-item>
          <b-dropdown-item>Second Action</b-dropdown-item>
          <b-dropdown-item>Third Action</b-dropdown-item>
          <b-dropdown-divider></b-dropdown-divider>
          <b-dropdown-item active>Active action</b-dropdown-item>
          <b-dropdown-item disabled>Disabled action</b-dropdown-item>
        </b-dropdown>
      </div>

    </div>
    <div class="row">
      <div class="col-sm-6 offset-sm-3 text-center">
		<b-form-textarea
          id="textarea"
          v-model="inStmt"
          placeholder="Enter something..."
          rows="3"
          max-rows="6"
        ></b-form-textarea>
      </div>
      <div class="col-sm-6 offset-sm-6 text-center">
		<b-form-textarea
          id="textarea"
          v-model="outStmt"
          placeholder="Enter something..."
          rows="3"
          max-rows="6"
        ></b-form-textarea>
      </div>
    </div>
    <div class="row" v-show="showSpoiler">
      <div class="col-sm-6 offset-sm-3 text-center">
        <span class="text-muted font-italic">{{taskData.srcWordTrnsLbl}} - {{taskData.srcWordRentaikeiLbl}} - {{taskData.srcWordHiraganaLbl}}</span>
      </div>
    </div>

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

    <div class="form-text"></div>

    <div class="row">
      <div class="col-sm-2 offset-sm-3 text-center" v-show="!taskDone">
        <button type="button" class="btn btn-danger" v-on:click="giveUp()">Show solution</button>
      </div>

      <div class="col-sm-2 offset-sm-3 text-center" v-show="taskDone">
        <button type="button" class="btn btn-success" v-on:click="newTask()">Next</button>
      </div>

    </div>


  </div>
</template>

<script>
export default {
  name: 'AppHome',
  data() {
    return {
      outStmt: null,
      taskData:{},
      taskSuccess: false,
      taskDone: false,
      taskFail: false,
      showSpoiler: false,
      answer: ''
    };
  },
  watch: {
    'taskDone'(v) {
      this.showSpoiler = v;
    }
  },
  created() {
    this.newTask();
  },
  methods: {

    newTask() {
      do {
        this.newTaskCore();
      } while(this.taskDone);
    },

    // Sometimes it happens that a task is immediately solved, in that case just generate a new one
    newTaskCore() {
      this.taskData = pickTask();
      this.taskDone = false;
      this.taskFail = false;
      this.taskSuccess = false;
      this.answer = this.taskData.srcWordKanjiLbl;
      this.validate();
    },

    giveUp() {
      axios.get('http://localhost:7531/rewrite', {
params: { profile: 'jena', query: this.inStmt }
         })
        .then(response => this.outStmt = response.data);
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
</style>

<style>
    html {
      width: 100%;
      height: 100%;
    }

    body {
      background: url("Djamila_Knopf_Childhood_Memories.jpg") no-repeat center center fixed;
      -webkit-background-size: cover;
      -moz-background-size: cover;
      -o-background-size: cover;
      background-size: cover;
    }
</style>
