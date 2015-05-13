package org.colombiamovil.smartkpi.client.ui.widgets;

import java.util.Vector;

import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.ButtonBase;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.HasValue;

public class MultipleStateBox extends ButtonBase implements HasName, HasValue<String>
{

    public static String UNCHECKED = "unchecked", CHECKED = "checked", AVG = "avg", SUM = "sum", MAX = "max", MIN = "min";
    protected static String imgSrcPath = "images/msb/", imgSrcExt = "_mini.gif";
    protected Vector<String> values;

    protected String name;
    protected String value = UNCHECKED;
    protected ImageElement imgElement;
    protected LabelElement lblElement;
    protected boolean valueChangeHandlerInitialized;
    protected String title = "";
    protected InfoWidget description;
    // private String valueBeforeClick;

    public MultipleStateBox( )
    {
        this( DOM.createImg( ) );
    }

    /**
     * @wbp.parser.constructor
     */
    public MultipleStateBox( String text )
    {
        this( );
        setText( text );
    }

    protected MultipleStateBox( Element elem )
    {
        super( DOM.createSpan( ) );
        values = new Vector<String>( );
        setBooleanValues( );
        imgElement = ImageElement.as( elem );
        imgElement.setWidth( 12 );
        imgElement.setHeight( 12 );

        lblElement = Document.get( ).createLabelElement( );

        getElement( ).appendChild( imgElement );
        getElement( ).appendChild( lblElement );
        String uid = DOM.createUniqueId( );
        imgElement.setPropertyString( "id", uid );
        // lblElement.setHtmlFor(uid);
        // setTabIndex(0);
        // addClickHandler(this);
        setValue( UNCHECKED );
    }

    public String getName( )
    {
        return name;
    }

    public void setName( String name )
    {
        this.name = name;
    }

    public String getValue( )
    {
        return value;
    }

    public void setValue( String value )
    {
        setValue( value, true );
    }

    public void changeValue( )
    {
        setValue( values.get( ( values.indexOf( getValue( ) ) + 1 ) % values.size( ) ) );
    }

    public void setPossibleValues( String[] valuesArray )
    {
        values.clear( );
        for( String v : valuesArray )
        {
            values.add( v );
        }
    }

    public void setBooleanValues( )
    {
        setPossibleValues( new String[]{ UNCHECKED, CHECKED } );
    }

    public void setAggValues( )
    {
        setPossibleValues( new String[]{ UNCHECKED, AVG, SUM, MAX, MIN } );
    }

    public void setAllValues( )
    {
        setPossibleValues( new String[]{ UNCHECKED, CHECKED, AVG, SUM, MAX, MIN } );
    }

    public void setValue( String value, boolean fireEvents )
    {
        // valueBeforeClick = getValue();
        this.value = value;
        imgElement.setSrc( imgSrcPath + value + imgSrcExt );
        if( fireEvents )
            ValueChangeEvent.fire( this, value );
    }

    public HandlerRegistration addValueChangeHandler( ValueChangeHandler<String> handler )
    {
        if( !valueChangeHandlerInitialized )
        {

            /*
             * this.addKeyUpHandler(new KeyUpHandler() { public void onKeyUp(KeyUpEvent event) { valueBeforeClick = getValue(); } });
             * 
             * this.addMouseUpHandler(new MouseUpHandler() { public void onMouseUp(MouseUpEvent event) { PopupMessage.showHelp("TEST"); } });
             */

//            this.addMouseOverHandler( new MouseOverHandler( )
//            {
//
//                public void onMouseOver( MouseOverEvent event )
//                {
//                    if( event.getSource( ) instanceof MultipleStateBox )
//                    {
//                        MultipleStateBox msb = ( MultipleStateBox )event.getSource( );
//                        if( msb.getDescription( ).length( ) > 0 )
//                            PopupMessage.showHelp( msb.getDescription( ) );
//                    }
//                }
//            } );

            this.addClickHandler( new ClickHandler( )
            {
                public void onClick( ClickEvent event )
                {
                    PopupMessage.hideHelp( );
                }
            } );
            /*
             * this.addMouseOutHandler(new MouseOutHandler() { public void onMouseOut(MouseOutEvent event) { PopupMessage.hideHelp(); } });
             */
            /*
             * this.addClickHandler(new ClickHandler() { public void onClick(ClickEvent event) { ValueChangeEvent.fireIfNotEqual(MultipleStateBox.this, valueBeforeClick,
             * getValue()); } });
             */

            valueChangeHandlerInitialized = true;
        }
        return addHandler( handler, ValueChangeEvent.getType( ) );
    }

    @Override
    public void setText( String text )
    {
        lblElement.setInnerText( text );
    }

    @Override
    public String getText( )
    {
        return lblElement.getInnerText( );
    }

    public void setDescription( String title )
    {
        this.title = title;
//        if( title.length( ) > 0 )
//        {
//            // System.out.println("Setting title");
//            description = new InfoWidget( PopupMessage.HELP_MESSAGE, InfoWidget.MOUSE_ACTION, title );
//            getElement( ).appendChild( description.getElement( ) );
//            // description.setTitle(title);
//        }
    }

    public String getDescription( )
    {
        return title;
    }
}
