package prime

import javax.swing.text.html.parser.Entity

import com.workday.montague.ccg.{CcgCat, NP, S, TerminalCat}
import com.workday.montague.parser.{IntegerMatcher, ParserDict}
import com.workday.montague.semantics.{λ, _}
import com.workday.montague.ccg
/**
  * Created by prime on 16/3/17.
  */

//TODO : events in chennai between 1st and 2nd
// TODO : see if me , my and I to filter to the users role.

object LexiconOps {

  val events2SemanticMap = Seq("event"->allEventType,"conference"->conference,"corporate"->corporate,"training"->training,"workshop"->workshop,"celebration"->celebrations);

  implicit class pluralOps(val str:String) extends AnyVal{
    def s:Seq[String] = Seq(str,str+"s");
  }

  implicit class seqPluralOps(val str:Seq[String]) extends AnyVal{
    def s:Seq[String] = str ++ str.map(x=>x+"s");
  }

  var lexicon = ParserDict[CcgCat]() +
/*    (("event".s) -> Seq((NP,Form(allEventType):SemanticState),
                        ((S/NP),λ { location: Location  =>  listEvents(allEventType,allRole,allEventCategory,Some(location))}), // events organized by me.
                        ((S\NP)/NP,λ { location: Location  =>λ { searchString: SearchString  =>  listEvents(allEventType,allRole,allEventCategory,Some(location),None,Some(searchString))}}), // events organized by me.
                        ((NP/NP),λ { n3:Role  =>  listEvents(allEventType,n3)}),
                        (((S/NP)/V/NP),λ { role:Role  => λ { dateEntity: DateEntity => listEvents(allEventType,role,allEventCategory,None,Some(dateEntity))}}),
                        ((NP\O),λ { searchString: SearchString  =>  listEvents(allEventType,allRole,allEventCategory,None,None,Some(searchString))}), // Ex. java events
                        ((NP/O),λ { searchString: SearchString  =>  listEvents(allEventType,allRole,allEventCategory,None,None,Some(searchString))}), // Ex. events related to java , events java
                        ((NP/NP),λ {lambda: Lambda[SemanticState] =>lambda(Form(allEventType))}),
                        ((NP/NP),λ {location: Location  =>  listEvents(allEventType,allRole,allEventCategory,Some(location))}), // my events in chennai
                        (((NP\O)/NP),λ {location: Location =>λ {searchString: SearchString  =>  listEvents(allEventType,allRole,allEventCategory,Some(location),None,Some(searchString))}}), // java events in chennai
                        (((NP\O)/NP),λ{ date:DateAndLocation=>λ{searchString: SearchString =>listEvents(allEventType,allRole,allEventCategory,Some(date.location),Some(date.dateEntity),Some(searchString))}}), // java events in chennai between 1st and 2nd
                        (((NP\O)/NP),λ {dateEntity: DateEntity =>λ {searchString: SearchString  =>  listEvents(allEventType,allRole,allEventCategory,None,Some(dateEntity),Some(searchString))}}), // java events happening this week
                        (((NP/NP)/O),λ {searchString: SearchString  =>λ {location: Location =>  listEvents(allEventType,allRole,allEventCategory,Some(location),None,Some(searchString))}}), // events related to java in chennai
                        (((NP/NP)/O),λ {searchString: SearchString  =>λ {dateAndLocation: DateAndLocation =>  listEvents(allEventType,allRole,allEventCategory,Some(dateAndLocation.location),Some(dateAndLocation.dateEntity),Some(searchString))}}), // events related to java in chennai between 1st and 2nd
                        (((NP/NP)/O),λ {searchString: SearchString  =>λ {dateEntity: DateEntity =>  listEvents(allEventType,allRole,allEventCategory,None,Some(dateEntity),Some(searchString))}}), // events related to java this week.
                        ((NP/NP),λ { date:DateEntity  =>  listEvents(allEventType,allRole,allEventCategory,None,Some(date))}) // events this week
                       )) +*/
    (Seq("that","i have") -> Seq(((NP\NP),identity),((NP/NP),identity),((NP),identity))) +
    (Seq("iam","i am") -> Seq(((NP/V),identity),(NP/NP,identity),((NP\NP)/NP,identity),(NP,identity))) +
    (Seq("participating","participant","a participant","participated","part of") ->(V,Form(participant):SemanticState)) +
    (Seq("speaking","a speaker","speaker") ->(V,Form(participant):SemanticState)) +
    (Seq("organizing","a organizer","organized") ->(V,Form(organizer):SemanticState)) +
    (Seq("what") ->(NP,identity)) +
    (Seq("me") ->(NP,identity)) +
   // (("between")->()) +
    (Seq("what are all","by") ->Seq(/*(NP/NP,identity),*/(S/NP,identity))) +
    (Seq("by") ->((NP\V)/NP,identity)) +
    (Seq("related") ->Seq(((NP\NP)\NP,identity),((NP\NP),identity),((NP\O),identity))) +
    (Seq("list all the","list the") ->(S/NP,identity)) +
    (Seq("are") ->(NP\NP,identity)) +
    (Seq("and") ->((NP\NP)/NP,λ {d1:Date =>λ {d2:Date =>DateRange(d1,d2)}})) +
    (Seq("happening") ->Seq((NP\NP,identity),
                            (NP/NP,identity))) +
    (Seq("between") -> Seq((((NP\NP)/NP),λ { dateRange:DateEntity => λ { location:Location => λ { eventType:EventType => listEvents(eventType,allRole,allEventCategory,Some(location),Some(dateRange))}}}),
                          (((NP\NP)/NP),λ { dateRange:DateEntity =>  λ { eventType:EventType => listEvents(eventType,allRole,allEventCategory,None,Some(dateRange))}}),
                          (((NP\NP)/NP),λ { dateRange:DateEntity =>  λ { location: Location=> DateAndLocation(location,dateRange)}}),
                          ((NP/NP),identity)
                          )) +
    (("run") -> ((NP/I),λ { cmd :InternalCommands =>(run(cmd))})) + // Internal commands
    (("regression") -> (I,Form(regression):SemanticState)) +
    (Seq("the") ->Seq(((NP/NP)\NP,identity),((NP/NP),identity),((NP\NP),identity))) +
    (Seq("my") ->Seq(((S/NP),λ { n3:EventType  => listEvents(n3)}),(S/NP,identity),(NP/NP,λ { n3:EventType  => listEvents(n3)}),(NP/NP,identity))) +
    (("in")->Seq((NP/NP,identity),((NP\NP)/NP,identity),
                ((((NP\NP)\NP)/NP),λ{ date:DateEntity=>{ location:Location=>λ{ eventType : EventType =>λ{ searchString:SearchString =>listEvents(eventType,allRole,allEventCategory,Some(location),Some(date),Some(searchString))}}}}),
                ((NP\V)/NP,λ { location:Location =>λ { role:Role =>λ { eventType: EventType => listEvents(eventType,role,allEventCategory,Some(location))}}}),
                ((NP\V)/NP,λ { dateEntity: DateEntity =>λ { role:Role =>λ { eventType: EventType => listEvents(eventType,role,allEventCategory,None,Some(dateEntity))}}}))) +
    //TODO : Change O to apps category and add lexicons
    (attributeMatcher("apps") -> (O,{app:String=>Form(SearchString(app))})) +
    (attributeMatcher("objects") -> (O,{app:String=>Form(SearchString(app))}))

  buildLexiconForEvents

  // (("chennai") -> (NP,Form(Location("Chennai")):SemanticState))


  /*  Injecting lexicon definition for the entities identified from the sentence - Lexicons are intended
      to be injected in run time. */

  def injectLexicon[T <: Entity](entity:T):Unit = {
      val lexeme:(String,(TerminalCat,SemanticState)) = entity match {
        case d@Location(value:String) => ((value) -> (NP,Form(d):SemanticState))
        case d@DateString(value:String) => ((value) -> (NP,Form(d):SemanticState))
        case d@Date(value:String) => ((value) -> (NP,Form(d):SemanticState))
        case d@SearchString(value:String) => ((value) -> (O,Form(d):SemanticState))
      }
    this.lexicon+=lexeme
  }

  def buildLexiconForEvents:Unit={

    events2SemanticMap.foreach(ev => {
      val lexeme = ((ev._1).s -> Seq((NP,Form(ev._2):SemanticState),
        ((S/NP),λ { location: Location  =>  listEvents(ev._2,allRole,allEventCategory,Some(location))}), // events organized by me.
        ((S\NP)/NP,λ { location: Location  =>λ { searchString: SearchString  =>  listEvents(ev._2,allRole,allEventCategory,Some(location),None,Some(searchString))}}), // events organized by me.
        ((NP/NP),λ { n3:Role  =>  listEvents(ev._2,n3)}),
        (((S/NP)/V/NP),λ { role:Role  => λ { dateEntity: DateEntity => listEvents(ev._2,role,allEventCategory,None,Some(dateEntity))}}),
        ((NP\O),λ { searchString: SearchString  =>  listEvents(ev._2,allRole,allEventCategory,None,None,Some(searchString))}), // Ex. java events
        ((NP/O),λ { searchString: SearchString  =>  listEvents(ev._2,allRole,allEventCategory,None,None,Some(searchString))}), // Ex. events related to java , events java
        ((NP/NP),λ {lambda: Lambda[SemanticState] =>lambda(Form(ev._2))}),
        ((NP/NP),λ {location: Location  =>  listEvents(ev._2,allRole,allEventCategory,Some(location))}), // my events in chennai
        (((NP\O)/NP),λ {location: Location =>λ {searchString: SearchString  =>  listEvents(ev._2,allRole,allEventCategory,Some(location),None,Some(searchString))}}), // java events in chennai
        (((NP\O)/NP),λ{ date:DateAndLocation=>λ{searchString: SearchString =>listEvents(ev._2,allRole,allEventCategory,Some(date.location),Some(date.dateEntity),Some(searchString))}}), // java events in chennai between 1st and 2nd
        (((NP\O)/NP),λ {dateEntity: DateEntity =>λ {searchString: SearchString  =>  listEvents(ev._2,allRole,allEventCategory,None,Some(dateEntity),Some(searchString))}}), // java events happening this week
        (((NP/NP)/O),λ {searchString: SearchString  =>λ {location: Location =>  listEvents(ev._2,allRole,allEventCategory,Some(location),None,Some(searchString))}}), // events related to java in chennai
        (((NP/NP)/O),λ {searchString: SearchString  =>λ {dateAndLocation: DateAndLocation =>  listEvents(ev._2,allRole,allEventCategory,Some(dateAndLocation.location),Some(dateAndLocation.dateEntity),Some(searchString))}}), // events related to java in chennai between 1st and 2nd
        (((NP/NP)/O),λ {searchString: SearchString  =>λ {dateEntity: DateEntity =>  listEvents(ev._2,allRole,allEventCategory,None,Some(dateEntity),Some(searchString))}}), // events related to java this week.
        ((NP/NP),λ { date:DateEntity  =>  listEvents(ev._2,allRole,allEventCategory,None,Some(date))}) // events this week
      ))
      this.lexicon += lexeme
    })

  }

}

