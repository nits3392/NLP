
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author nitsn
 */
public class ViterbiTagger {

    private static Map<String, Map<String, Double>> Emit;
    private static Map<String, Map<String, Double>> Trans;
    private static Map<String, Map<String, Double>> EmitProb;
    private static Map<String, Map<String, Double>> EmitWordState;
    private static Map<String, Map<String, Double>> TransProb;
    private static List<Map<String, PathTrace>> TagDecider;
    //private static Map<String, PathTrace> UnknownTagDecider;

    public static void BuildDatabase(String sentence) {
        //Trans.put("Start",new HashMap<String,Double>());
        String[] sub = sentence.split("\\n");

        for (int i = 0; i < sub.length; i++) {
            // first is word , next item is its tag
            String word = sub[i].split("\\t")[0];
            String tag = sub[i].split("\\t")[1];
            //System.out.println(word+"    "+tag);

            //populate Emission database
            if (!Emit.containsKey(tag))// map does not contain tag 
            {

                Map<String, Double> wordcount = new HashMap<String, Double>();
                wordcount.put(word, 1.0);
                Emit.put(tag, wordcount);

            } else if (!Emit.get(tag).containsKey(word))// no word associtaed with tag
            {
                Emit.get(tag).put(word, 1.0);
            } else// map contains tag and tag contains word. increment count
            {

                double occurence = Emit.get(tag).get(word) + 1;
                Emit.get(tag).put(word, occurence);
            }

            // populate transmission database
            if (i < sub.length - 1) {

                String nextTag = sub[i + 1].split("\\t")[1];
                if (Trans.containsKey(tag)) // if tag to state not present put in map
                {

                    if (Trans.get(tag).containsKey(nextTag)) {
                        //increment next tag transition by 1
                        double occurence = Trans.get(tag).get(nextTag) + 1;
                        Trans.get(tag).put(nextTag, occurence);
                    } else {
                        Trans.get(tag).put(nextTag, 1.0);

                    }
                } //else increment count of already present transition pair
                else {
                    Map<String, Double> Nextstate = new HashMap<String, Double>();
                    Nextstate.put(nextTag, 1.0);
                    Trans.put(tag, Nextstate);
                }

            }

        }

    }

    public static void CalculateProb() {

        for (String tag : TransProb.keySet()) {
            double Occurence = 0;

            for (String ntag : TransProb.get(tag).keySet()) {
                Occurence += TransProb.get(tag).get(ntag);

            }

            for (String ntag : TransProb.get(tag).keySet()) {
                // compute log probabilities
                double TProb = TransProb.get(tag).get(ntag) / Occurence;
                Trans.get(tag).put(ntag, TProb);
            }
        }

        for (String tag : EmitProb.keySet()) {
            double Occurence = 0;

            for (String word : EmitProb.get(tag).keySet()) {
                Occurence += EmitProb.get(tag).get(word);
            }

            for (String word : EmitProb.get(tag).keySet()) {
                // log calculate
                double EProb = EmitProb.get(tag).get(word) / Occurence;
                EmitProb.get(tag).put(word, EProb);
            }

        }

        Map<String, Double> wordcount = new HashMap<String, Double>();
        Map<String, Double> test = null;
        EmitWordState = new HashMap<String, Map<String, Double>>();

        Map<String, Double> innerMap = null;
        for (String a : EmitProb.keySet()) {
            for (String b : EmitProb.get(a).keySet()) {
                Double prob = EmitProb.get(a).get(b);
//                System.out.println(a + "  " + b + "  " + prob);
                //populate Emission word as key - state as value database

                if (EmitWordState.containsKey(b)) {
//                    System.out.println("old word: "+b);
                    innerMap = EmitWordState.get(b);
                    innerMap.put(a, prob);
                    //EmitWordState.put(b, innerMap);
                } else {
                    test = new HashMap<String, Double>();
                    test.put(a, prob);
                    EmitWordState.put(b, test);
                }
            }
        }

        //System.out.println("WOrd as key state as value");
//        for (String a : EmitWordState.keySet()) {
//            for (String b : EmitWordState.get(a).keySet()) {
//                double prob = EmitWordState.get(a).get(b);
//                
//               System.out.println("" + a + "  " + b + " " + prob);
//            }
//        }
    }

    
 
public static String Improveunknown(String word,String pre)
{
	int length = word.length();
        String state="";
	
	if(pre.equals("XXX"))
	{
		if (length >= 2 &&word.substring(length - 2, length).equals("ss"))
		{
		  state = "NN";
		} else if (length >= 1&&word.substring(length - 1, length).equals("s")) 
		{
		  state = "NNS";
		} else if (word.contains("-")) 
		{
		  state = "JJ";
		} else if (word.contains(".")) 
		{
		  state = "CD";
		}  else if (length >= 3 && word.substring(length - 3, length).equals("ble")) 
		{
		  state = "JJ";
		} else if (length >= 3 && word.substring(length - 3, length).equals("ive")) 
		{
		  state = "JJ";
		} else if (length >= 2&&word.substring(length - 2, length).equals("us")) 
		{
		  state = "JJ";
		} else 
		{
		  state="Unimproved";
		}
	}
	
	else
	{
		if (Character.isUpperCase(word.charAt(0))) 
		{
		  state = "NNP";
		  
		} 
                
                else if (pre.equals("be")) 
                {
                    state = "JJ";
                } 
                else if (pre.equals("it")) 
                {
                    state = "VBZ";
                } 
                else if (pre.equals("would")) 
                {
                    state = "VB";
                }
		 else if (length >= 2 &&word.substring(length - 2, length).equals("ss"))
		{
		  state = "NN";
		} 
                 
                else if (length >= 1&&word.substring(length - 1, length).equals("s")) 
		{
		  state = "NNS";
		} 
                else if (word.contains("-")) 
		{
		  state = "JJ";
		} 
                else if (word.contains(".")) 
		{
		  state = "CD";
		}  
                else if (length >= 3 && word.substring(length - 3, length).equals("ble")) 
		{
		  state = "JJ";
		} 
                else if (length >= 3 && word.substring(length - 3, length).equals("ive")) 
		{
		  state = "JJ";
		} 
                else if (length >= 2&&word.substring(length - 2, length).equals("us")) 
		{
		  state = "JJ";
		} else 
		{
		  state="Unimproved";
		}
	
	}
	
	return state;
}
    public static List<String> Tagger(String SentencetoTag) {
        String outputString = "";
        TagDecider = new ArrayList<>();
        // PathTrace obj= new PathTrace();

        String[] words = SentencetoTag.split(" ");
        //System.out.println("Sentence to Tag" + "  " + SentencetoTag);

        for (int i = 0; i < words.length; i++) {
            // Iterate emission prob to check for that word what stae trensition occurs

            Map<String, PathTrace> BackTrack = new HashMap<>();
            Map<String, PathTrace> Current = new HashMap<>();
            if (EmitWordState.containsKey(words[i]))// map does not contain tag 
            {

                //System.out.println(" Tag is "+"  "+words[i]);
                double CurrProb = -0.1;
                PathTrace obj = null;
                //System.out.print("emit size"+EmitWordState.get(words[i]).size());
                //obj.ProbTags=-10.0;
                for (String state : EmitWordState.get(words[i]).keySet()) {

                    //System.out.println(" I is " + "  " + i);
                    // System.out.println("  Word is found  " + words[i] + "  in state " + state);
                    //System.out.println("Path Trace initi: "+obj.ProbTags);
                    if (i == 0) {
                        obj = new PathTrace();
                        obj.ProbTags = -0.1;
                        //System.out.println(" I=0 "+obj.ProbTags+state);
                        //System.out.println(" I=0 "+obj.ProbTags+state);
                        CurrProb = EmitWordState.get(words[i]).get(state);
                        if (obj.ProbTags < CurrProb) {

                            obj.ProbTags = CurrProb;
                            //obj.SequenceofTags.add(state);
                            //Current.put(state,obj);  
                            //System.out.println(" I=0 "+obj.ProbTags+state);

                        }
                        //TagDecider.add(Current);
                        // System.out.println(" I=0 "+obj.ProbTags+state);
                    } else {
                        //System.out.println(" I= "+i);
                        obj = new PathTrace();
                        BackTrack = TagDecider.get(i - 1);
                        //System.out.println("  BackTrack keys  " + BackTrack.keySet());

                        //PathTrace temp=new PathTrace();
                        String StateToAdd = "";
                        obj.ProbTags = -0.1;
                        for (String PrevState : BackTrack.keySet()) {
                            //System.out.println("In back track Loop " + PrevState);
                            //StateToAdd = state;
                            //  System.out.println(" I= "+i);
                            //System.out.println("  BackTrack state  " + PrevState);
                            PathTrace prevalue = BackTrack.get(PrevState);

                            if (TransProb.get(PrevState).get(state) == null) {
                                //System.out.println("Trans is zero");
                                CurrProb = EmitProb.get(state).get(words[i]) * 0.0 * (prevalue.ProbTags);
                            } else {// Get all possible and calculate prev state from backtrack
                                CurrProb = EmitProb.get(state).get(words[i]) * (TransProb.get(PrevState).get(state)) * (prevalue.ProbTags);
                            }

                            //System.out.println("Prev prob "+prevalue.ProbTags);
                            if (obj.ProbTags < CurrProb) {
                                //System.out.println(" Previous seq of tags :"+prevalue.SequenceofTags );
                                //System.out.println("I am here to check ");
                                //temp=prevalue;
                                obj.ProbTags = CurrProb;
                                //System.out.println("I is =  "+i);
                                obj.SequenceofTags.clear();

                                // System.out.println("Sequence cleared)");
                                obj.SequenceofTags.addAll(prevalue.SequenceofTags);
                                // System.out.println("Previous Sequence  " + prevalue.SequenceofTags);
                                //                               temp.SequenceofTags=(prevalue.SequenceofTags);
                                obj.SequenceofTags.add(PrevState);
                                //System.out.println("Updated Sequence  " + obj.SequenceofTags);
                                StateToAdd = state;

                                //Current.put(tag,obj);
                                //TagDecider.add(Current);
                                //System.out.println("I am here to check sequence of tags "+obj.SequenceofTags);
                            }

                        }

                        //System.out.println("I am here to check state to add "+StateToAdd+" current back state "+state);
                    }

                    Current.put(state, obj);
                    //System.out.println("Value put in hash map : state " + state);
                    //obj.SequenceofTags=temp.SequenceofTags;
                    //obj.SequenceofTags.add(tag);

                }
                //System.out.println("\n");
                //System.out.println("  value of i is:  " + i);
            } else // word not available in corpus
            {
                //System.out.println("  value of i is:  " + i);
                //System.out.println("  Unknown words  " + words[i]);
                double CurrProb = -0.1;
                PathTrace obj = null;
                String currExpectedstate="";
                
                if(i==0)
                {
                    currExpectedstate=Improveunknown(words[i],"XXX");
                }
               else
                {
                    currExpectedstate=Improveunknown(words[i],words[i-1]);
                }
                
                if(currExpectedstate.equals("Unimproved"))
               {
                // Current = new HashMap<>();
                for (String state : EmitProb.keySet()) {
//                    System.out.println("  value of i is:  " + i);

                    if (i == 0) {
                          obj = new PathTrace();
                         obj.ProbTags = -0.1;
                        //System.out.println(" I=0 "+obj.ProbTags+state);
                        //System.out.println(" I=0 "+obj.ProbTags+state);
                        CurrProb = 0.1;
                        if (obj.ProbTags < CurrProb) {

                            obj.ProbTags = CurrProb;
                            

                        }

                    } else {

                        BackTrack = TagDecider.get(i - 1);
                         obj = new PathTrace();
//                    System.out.println("  BackTrack keys  " + BackTrack.keySet());
                        obj.ProbTags = -0.1;
                        for (String PrevState : BackTrack.keySet()) {
                            //System.out.println("In back track Loop " + PrevState);
                            //StateToAdd=state;
                            //  System.out.println(" I= "+i);
                            //System.out.println("  BackTrack state  " + PrevState);
                            PathTrace prevalue = BackTrack.get(PrevState);

                            if (TransProb.get(PrevState).get(state) == null) {
                                //System.out.println("Trans is zero");
                                CurrProb = 0.1 * 0.0 * (prevalue.ProbTags);
                            } else {// Get all possible and calculate prev state from backtrack
                                CurrProb = 0.1 * (TransProb.get(PrevState).get(state)) * (prevalue.ProbTags);
                            }

                            //System.out.println("Prev prob "+prevalue.ProbTags);
                            if (obj.ProbTags < CurrProb) {
                                //System.out.println(" Previous seq of tags :"+prevalue.SequenceofTags );
                                //System.out.println("I am here to check ");
                                //temp=prevalue;
                                obj.ProbTags = CurrProb;
                                //System.out.println("I is =  "+i);
                                obj.SequenceofTags.clear();

                                //System.out.println("Sequence cleared)");
                                obj.SequenceofTags.addAll(prevalue.SequenceofTags);
                                // System.out.println("Previous Sequence  " + prevalue.SequenceofTags);
                                //                               temp.SequenceofTags=(prevalue.SequenceofTags);
                                obj.SequenceofTags.add(PrevState);
                                // System.out.println("Updated Sequence  " + obj.SequenceofTags);
                                //StateToAdd = state;

                                //Current.put(tag,obj);
                                //TagDecider.add(Current);
                                //System.out.println("I am here to check sequence of tags "+obj.SequenceofTags);
                            }

                        }

                    }
                     Current.put(state, obj);
                }
            }
            else
            {   
                
                if(i==0)
                {
                    obj = new PathTrace();
                    obj.ProbTags=0.6;
                    
                }
                else
                {
                       BackTrack = TagDecider.get(i - 1);
                         obj = new PathTrace();
//                    System.out.println("  BackTrack keys  " + BackTrack.keySet());
                        obj.ProbTags = -0.1;
                        
                        for (String PrevState : BackTrack.keySet()) 
                        {
                            PathTrace prevalue = BackTrack.get(PrevState);
                            if(TransProb.get(PrevState).get(currExpectedstate)!=null)
                            {
                                if(TransProb.get(PrevState).get(currExpectedstate)>obj.ProbTags)
                                {
                                    obj.ProbTags=0.6;
                                    obj.SequenceofTags.clear();

                                    // System.out.println("Sequence cleared)");
                                    obj.SequenceofTags.addAll(prevalue.SequenceofTags);
                                    // System.out.println("Previous Sequence  " + prevalue.SequenceofTags);
                                    //                               temp.SequenceofTags=(prevalue.SequenceofTags);
                                    obj.SequenceofTags.add(PrevState);
                                }
                            }
                        }
                }
                
                Current.put(currExpectedstate, obj);
            }
                
                                       

                //TagDecider.add(UnknownWordMap);

                //Current.put(state,obj);
            }

            // one array list element to be put each time
//            for(String iter: Current.keySet()) {
////                if(words[i].equalsIgnoreCase("be")) {}
//                System.out.println("word is: "+words[i]+" state:"+iter+ "prob: "+ Current.get(iter).ProbTags);
//            }
            TagDecider.add(Current);
            //System.out.println("before clearing current hashmap "+TagDecider.get(i).entrySet());
            //Current.clear();
            //System.out.println("Next word " + "\n");
        }

        Map<String, PathTrace> finalHash = new HashMap<>();
        finalHash = TagDecider.get(words.length - 1); // return the highest prob values from last element of list
        PathTrace MaxProb = new PathTrace();
        for (String State : finalHash.keySet()) {
            PathTrace currvalue = finalHash.get(State);

            if (currvalue.ProbTags > MaxProb.ProbTags) {
                MaxProb.ProbTags = currvalue.ProbTags;
                MaxProb.SequenceofTags.clear();
                MaxProb.SequenceofTags.addAll(currvalue.SequenceofTags);
                MaxProb.SequenceofTags.add(State);
            }

        }

        //System.out.println("Size of array list  "+TagDecider.size());
        return MaxProb.SequenceofTags;
    }

    public static void ReadWriteFile(String ViterbiTagFile) {
        String sentence = "";
        String line = "";
        String SentenceTag = "";
        BufferedReader inputbuff = null;
        BufferedWriter outputbuff = null;
        //System.out.println("I am here");
        try {
            inputbuff = new BufferedReader(new FileReader(ViterbiTagFile));
            outputbuff = new BufferedWriter(new FileWriter("wsj_23.pos"));
            while ((line = inputbuff.readLine()) != null)// read end of file         
            {
                //System.out.println("I am here also");
                if (!line.isEmpty()) {
                    sentence = sentence + line + " ";
                    //System.out.println(sentence);
                } else // tag this sentence 
                {
                    //System.out.println("Here as well");
                    List<String> Tags = Tagger(sentence);
                    String[] Words = sentence.split(" ");
                   // System.out.println(" Word length = " + Words.length + "  Tag length =  " + Tags.size());
                    for (int i = 0; i < Words.length; i++) {
                        //Write to output file
                        outputbuff.write(Words[i] + "\t" + Tags.get(i) + "\n");
                        //outputbuff.write("\n");
                        //System.out.println(" " +Words[i] + "  " + Tags.get(i));

                    }
                    sentence = "";
                    outputbuff.write("\n");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            inputbuff.close();
            outputbuff.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //System.out.println("Hi I am here");
        String Traindatabase = "WSJ_02-21.pos";
        String ViterbiTagFile = "WSJ_23.words";
        Emit = new HashMap<String, Map<String, Double>>();
        Trans = new HashMap<String, Map<String, Double>>();
        EmitProb = new HashMap<String, Map<String, Double>>();
        TransProb = new HashMap<String, Map<String, Double>>();
        EmitWordState = new HashMap<>();

        BufferedReader buff = null;
        try {
            buff = new BufferedReader(new FileReader(Traindatabase));
            String line;
            String sentence = "";
            while ((line = buff.readLine()) != null)// read end of file
            {
                if (!line.isEmpty())// checking for new sentence
                {
                    sentence += line + "\n";
                } else {
                    BuildDatabase(sentence);
                    sentence = "";
                }
            }

            buff.close();
        } catch (Exception exc) {
            exc.printStackTrace();
        }

        try {
            buff.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
        EmitProb = Emit;
        TransProb = Trans;
        CalculateProb();
        ReadWriteFile(ViterbiTagFile);

    }

}

class PathTrace {

    public double ProbTags;
    public List<String> SequenceofTags;

    PathTrace() {
        ProbTags = -0.1;
        SequenceofTags = new ArrayList<>();

    }
}
