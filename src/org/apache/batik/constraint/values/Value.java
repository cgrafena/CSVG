package org.apache.batik.constraint.values;

import java.util.Iterator;
import java.util.List;

import javax.xml.transform.TransformerException;

import org.apache.batik.anim.Animation;
import org.apache.batik.anim.ConstraintsAnimationEngine;
import org.apache.batik.constraint.Constraint;
import org.apache.batik.constraint.ConstraintEngine;
import org.apache.batik.constraint.PrefixResolver;
import org.apache.batik.constraint.TypeErrorException;
import org.apache.batik.constraint.XPathException;
import org.apache.batik.constraint.xpath.Operators;
import org.apache.batik.constraint.xpath.XConstraintObject;
import org.apache.batik.dom.svg.SVGOMDocument;
import org.apache.batik.dom.util.DOMUtilities;
import org.apache.batik.dom.util.DoublyIndexedTable;
import org.apache.batik.util.SVGConstants;
import org.apache.xpath.objects.XObject;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * Abstract class that represents an SVG value to be used in XPath expressions.
 */
public abstract class Value implements SVGConstants, Operators {

    // The SVG types we handle.
	public static final int TYPE_UNKNOWN = 0;
	public static final int TYPE_STRING = 1;
	public static final int TYPE_BOOLEAN = 2;
	public static final int TYPE_INTEGER = 3;
	public static final int TYPE_NUMBER = 4;
	public static final int TYPE_LENGTH = 5;
	public static final int TYPE_COORDINATE = 6;
	public static final int TYPE_NUMBER_OPTIONAL_NUMBER = 7;
	public static final int TYPE_ANGLE = 8;
	public static final int TYPE_COLOR = 9;
	public static final int TYPE_PAINT = 10;
	public static final int TYPE_TRANSFORM_LIST = 11;
	public static final int TYPE_URI = 12;
	public static final int TYPE_FREQUENCY = 13;
	public static final int TYPE_TIME = 14;
	public static final int TYPE_ACCUMULATE_VALUE = 15;
	public static final int TYPE_ALIGNMENT_BASELINE_VALUE = 16;
	public static final int TYPE_BASELINE_SHIFT_VALUE = 17;
	public static final int TYPE_CALCMODE_VALUE = 18;
	public static final int TYPE_CLASSLIST = 19;
	public static final int TYPE_CLIP_VALUE = 20;
	public static final int TYPE_OPTIONAL_URI = 21;
	public static final int TYPE_CLIP_FILL_RULE = 22;
	public static final int TYPE_UNITS_VALUE = 23;
	public static final int TYPE_COLOR_INTERPOLATION_VALUE = 24;
	public static final int TYPE_COLOR_IMAGE_RENDERING_VALUE = 25;
	public static final int TYPE_CONTENT_TYPE = 26;
	public static final int TYPE_CURSOR_VALUE = 27;
	public static final int TYPE_PATH_DATA = 28;
	public static final int TYPE_DIRECTION_VALUE = 29;
	public static final int TYPE_DISPLAY_VALUE = 30;
	public static final int TYPE_DOMINANT_BASELINE_VALUE = 31;
	public static final int TYPE_LENGTHS = 32;
	public static final int TYPE_EDGE_MODE_VALUE = 33;
	public static final int TYPE_ENABLE_BACKGROUND_VALUE = 34;
	public static final int TYPE_FILL_VALUE = 35;
	public static final int TYPE_OPACITY_VALUE = 36;
	public static final int TYPE_SVG_COLOR = 37;
	public static final int TYPE_FONT_FAMILY_VALUE = 38;
	public static final int TYPE_FONT_SIZE_VALUE = 39;
	public static final int TYPE_FONT_SIZE_ADJUST_VALUE = 40;
	public static final int TYPE_FONT_STRETCH_VALUE = 41;
	public static final int TYPE_FONT_STYLE_VALUE = 42;
	public static final int TYPE_FONT_VARIANT_VALUE = 43;
	public static final int TYPE_FONT_WEIGHT_VALUE = 44;
	public static final int TYPE_GLYPH_ORIENTATION_HORIZONTAL_VALUE = 45;
	public static final int TYPE_GLYPH_ORIENTATION_VERTICAL_VALUE = 46;
	public static final int TYPE_KERNING_VALUE = 47;
	public static final int TYPE_LANGUAGE_CODES = 48;
	public static final int TYPE_LENGTH_ADJUST_VALUE = 49;
	public static final int TYPE_SPACING_VALUE = 50;
	public static final int TYPE_MARKER_UNITS_VALUE = 51;
	public static final int TYPE_MEDIA_DESC = 52;
	public static final int TYPE_METHOD_VALUE = 53;
	public static final int TYPE_MODE_VALUE = 54;
	public static final int TYPE_NUMBER_OR_PERCENTAGE = 55;
	public static final int TYPE_SCRIPT = 56;
	public static final int TYPE_MORPHOLOGY_OPERATOR_VALUE = 57;
	public static final int TYPE_COMPOSITE_OPERATOR_VALUE = 58;
	public static final int TYPE_OVERFLOW_VALUE = 59;
	public static final int TYPE_POINTER_EVENTS_VALUE = 60;
	public static final int TYPE_POINTS = 61;
	public static final int TYPE_PRESERVE_ASPECT_RATIO_SPEC = 62;
	public static final int TYPE_RENDERING_INTENT_VALUE = 63;
	public static final int TYPE_EXTENSION_LIST = 64;
	public static final int TYPE_RESTART_VALUE = 65;
	public static final int TYPE_NUMBERS = 66;
	public static final int TYPE_SHAPE_RENDERING_VALUE = 67;
	public static final int TYPE_MATRIX = 68;
	public static final int TYPE_SPREAD_METHOD_VALUE = 69;
	public static final int TYPE_STITCH_TILES_VALUE = 70;
	public static final int TYPE_STROKE_DASHARRAY_VALUE = 71;
	public static final int TYPE_LENGTH_OR_INHERIT = 72;
	public static final int TYPE_STROKE_LINECAP_VALUE = 73;
	public static final int TYPE_STROKE_LINEJOIN_VALUE = 74;
	public static final int TYPE_STROKE_MITERLIMIT_VALUE = 75;
	public static final int TYPE_STYLESHEET = 76;
	public static final int TYPE_LINK_TARGET = 77;
	public static final int TYPE_TEXT_ANCHOR_VALUE = 78;
	public static final int TYPE_TEXT_DECORATION_VALUE = 79;
	public static final int TYPE_TEXT_RENDERING_VALUE = 80;
	public static final int TYPE_TURBULENCE_TYPE_VALUE = 81;
	public static final int TYPE_COMPONENT_TRANSFER_TYPE_VALUE = 82;
	public static final int TYPE_COLOR_MATRIX_TYPE_VALUE = 83;
	public static final int TYPE_ANIMATE_TRANSFORM_TYPE_VALUE = 84;
	public static final int TYPE_UNICODE_BIDI_VALUE = 85;
	public static final int TYPE_VIEW_BOX_SPEC = 86;
	public static final int TYPE_VISIBILITY_VALUE = 87;
	public static final int TYPE_WRITING_MODE_VALUE = 88;
	public static final int TYPE_COORDINATES = 89;
	public static final int TYPE_CHANNEL_SELECTOR_VALUE = 90;
	public static final int TYPE_ZOOM_AND_PAN_VALUE = 91;
    public static final int TYPE_RECT = 92;
    public static final int TYPE_POINT = 93;
    public static final int TYPE_POINT_LIST = 94;
    public static final int TYPE_NUMBER_LIST = 95;
    public static final int TYPE_LENGTH_LIST = 96;
    public static final int TYPE_NODESET = 97;

	public static final int TYPE_LAST = 97;

    /**
     * An array of strings which are names for each type.
     */
	public static String[] TYPENAMES = {
        "Unknown", // 0
        "String", // 1
        "Boolean", // 2
        "Integer", // 3
        "Number", // 4
        "Length", // 5
        "Coordinate", // 6
        "NumberOptionalNumber", // 7
        "Angle", // 8
        "Color", // 9
        "Paint", // 10
        "TransformList", // 11
        "URI", // 12
        "Frequency", // 13
        "Time", // 14
        "AccumulateValue", // 15
        "AlignmentBaselineValue", // 16
        "BaselineShiftValue", // 17
        "CalcModeValue", // 18
        "ClassList", // 19
        "ClipValue", // 20
        "OptionalURI", // 21
        "ClipFillRule", // 22
        "UnitsValue", // 23
        "ColorInterpolationValue", // 24
        "ColorImageRenderingValue", // 25
        "ContentType", // 26
        "CursorValue", // 27
        "PathData", // 28
        "DirectionValue", // 29
        "DisplayValue", // 30
        "DominantBaselineValue", // 31
        "Lengths", // 32
        "EdgeModeValue", // 33
        "EnableBackgroundValue", // 34
        "FillValue", // 35
        "OpacityValue", // 36
        "SVGColor", // 37
        "FontFamilyValue", // 38
        "FontSizeValue", // 39
        "FontSizeAdjustValue", // 40
        "FontStretchValue", // 41
        "FontStyleValue", // 42
        "FontVariantValue", // 43
        "FontWeightValue", // 44
        "GlyphOrientationHorizontalValue", // 45
        "GlyphOrientationVerticalValue", // 46
        "KerningValue", // 47
        "LanguageCodes", // 48
        "LengthAdjustValue", // 49
        "SpacingValue", // 50
        "MarkerUnitsValue", // 51
        "MediaDesc", // 52
        "MethodValue", // 53
        "ModeValue", // 54
        "NumberOrPercentage", // 55
        "Script", // 56
        "MorphologyOperatorValue", // 57
        "CompositeOperatorValue", // 58
        "OverflowValue", // 59
        "PointerEventsValue", // 60
        "Points", // 61
        "PreserveAspectRatioSpec", // 62
        "RenderingIntentValue", // 63
        "ExtensionList", // 64
        "RestartValue", // 65
        "Numbers", // 66
        "ShapeRenderingValue", // 67
        "Matrix", // 68
        "SpreadMethodValue", // 69
        "StitchTilesValue", // 70
        "StrokeDashArrayValue", // 71
        "LengthOrInherit", // 72
        "StrokeLinecapValue", // 73
        "StrokeLinejoinValue", // 74
        "MiterLimitValue", // 75
        "Stylesheet", // 76
        "LinkTarget", // 77
        "TextAnchorValue", // 78
        "TextDecorationValue", // 79
        "TextRenderingValue", // 80
        "TurbulenceTypeValue", // 81
        "ComponentTransferTypeValue", // 82
        "ColorMatrixTypeValue", // 83
        "AnimateTransform", // 84
        "UnicodeBidiValue", // 85
        "ViewBoxSpec", // 86
        "VisibilityValue", // 87
        "WritingModeValue", // 88
        "Coordinates", // 89
        "ChannelSelectorValue", // 90
        "ZoomAndPanValue", // 91
        "Rect", // 92
        "Point", // 93
        "PointList", // 94
        "NumberList", // 95
        "LengthList", // 96
        "NodeSet" // 97
	};

    // /**
    //  * The ConstraintEngine using this Value.
    //  */
    // protected ConstraintEngine constraintEngine;

    /**
     * The type of this value.
     */
    protected int type;

    /**
     * Creates a new Value with any type.
     */
    public Value(/*ConstraintEngine ce, */int t) {
        // constraintEngine = ce;
        type = t;
    }
    
    /**
     * Table that maps (elementNamspaceURI:elementLocalName,
     * attributeNamespaceURI:attributeLocalName)
     * pairs to an attribute type int.
     */
    protected static DoublyIndexedTable/*2*/ attributeTypeTable = new DoublyIndexedTable/*2*/();
    
//     public static class DoublyIndexedTable2 extends DoublyIndexedTable {
//         public void dump() {
//             for (int i = 0; i < table.length; i++) {
//                 Entry e = table[i];
//                 while (e != null) {
//                     String en = (String) e.key1;
//                     String an = (String) e.key2;
//                     Integer j = (Integer) e.value;
//                     System.out.println(en + "\t" + an + "\t" + TYPENAMES[j.intValue()]);
//                     e = e.next;
//                 }
//             }
//         }
//     }
// 
//     public static void main(String args[]) {
//         attributeTypeTable.dump();
//     }
    
    /**
     * Add an attribute type to the attribute type table.
     */
    protected static void putAttributeType(String ens,
                                    String eln, 
                                    String ans, 
                                    String aln, 
                                    int t) {
        attributeTypeTable.put(ens + ":" + eln, ans + ":" + aln, new Integer(t));
    }
    
    static {
        String ns = SVG_NAMESPACE_URI;
        String any = "*";
        putAttributeType(ns, any, "", SVG_ACCENT_HEIGHT_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", "accumulate", TYPE_ACCUMULATE_VALUE);
        putAttributeType(ns, any, "", "alignment-baseline", TYPE_ALIGNMENT_BASELINE_VALUE);
        putAttributeType(ns, any, "", SVG_ALPHABETIC_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_AMPLITUDE_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_ARABIC_FORM_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_ASCENT_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", "attributeType", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_AZIMUTH_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_BASE_FREQUENCY_ATTRIBUTE, TYPE_NUMBER_OPTIONAL_NUMBER);
        putAttributeType(ns, any, "", "baseline-shift", TYPE_BASELINE_SHIFT_VALUE);
        putAttributeType(ns, any, "", "baseProfile", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_BBOX_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_BIAS_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", "by", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_CALC_MODE_ATTRIBUTE, TYPE_CALCMODE_VALUE);
        putAttributeType(ns, any, "", SVG_CAP_HEIGHT_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_CLASS_ATTRIBUTE, TYPE_CLASSLIST);
        putAttributeType(ns, any, "", "clip", TYPE_CLIP_VALUE);
        putAttributeType(ns, any, "", SVG_CLIP_PATH_ATTRIBUTE, TYPE_OPTIONAL_URI);
        putAttributeType(ns, any, "", "clip-rule", TYPE_CLIP_FILL_RULE);
        putAttributeType(ns, any, "", SVG_CLIP_PATH_UNITS_ATTRIBUTE, TYPE_UNITS_VALUE);
        putAttributeType(ns, any, "", "color", TYPE_COLOR);
        putAttributeType(ns, any, "", SVG_COLOR_INTERPOLATION_ATTRIBUTE, TYPE_COLOR_INTERPOLATION_VALUE);
        putAttributeType(ns, any, "", "color-interpolation-filters", TYPE_COLOR_INTERPOLATION_VALUE);
        putAttributeType(ns, any, "", "color-profile", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_COLOR_RENDERING_ATTRIBUTE, TYPE_COLOR_IMAGE_RENDERING_VALUE);
        putAttributeType(ns, any, "", SVG_CONTENT_SCRIPT_TYPE_ATTRIBUTE, TYPE_CONTENT_TYPE);
        putAttributeType(ns, any, "", SVG_CONTENT_STYLE_TYPE_ATTRIBUTE, TYPE_CONTENT_TYPE);
        putAttributeType(ns, any, "", "cursor", TYPE_CURSOR_VALUE);
        putAttributeType(ns, any, "", SVG_CX_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_CY_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_D_ATTRIBUTE, TYPE_PATH_DATA);
        putAttributeType(ns, any, "", SVG_DESCENT_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_DIFFUSE_CONSTANT_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", "direction", TYPE_DIRECTION_VALUE);
        putAttributeType(ns, any, "", "display", TYPE_DISPLAY_VALUE);
        putAttributeType(ns, any, "", SVG_DIVISOR_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", "dominant-baseline", TYPE_DOMINANT_BASELINE_VALUE);
        putAttributeType(ns, any, "", "dur", TYPE_STRING);
        putAttributeType(ns, SVG_TEXT_TAG, "", SVG_DX_ATTRIBUTE, TYPE_LENGTHS);
        putAttributeType(ns, SVG_TEXT_TAG, "", SVG_DY_ATTRIBUTE, TYPE_LENGTHS);
        putAttributeType(ns, SVG_TSPAN_TAG, "", SVG_DX_ATTRIBUTE, TYPE_LENGTHS);
        putAttributeType(ns, SVG_TSPAN_TAG, "", SVG_DY_ATTRIBUTE, TYPE_LENGTHS);
        putAttributeType(ns, SVG_TREF_TAG, "", SVG_DX_ATTRIBUTE, TYPE_LENGTHS);
        putAttributeType(ns, SVG_TREF_TAG, "", SVG_DY_ATTRIBUTE, TYPE_LENGTHS);
        putAttributeType(ns, SVG_ALT_GLYPH_TAG, "", SVG_DX_ATTRIBUTE, TYPE_LENGTHS);
        putAttributeType(ns, SVG_ALT_GLYPH_TAG, "", SVG_DY_ATTRIBUTE, TYPE_LENGTHS);
        putAttributeType(ns, SVG_GLYPH_REF_TAG, "", SVG_DX_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, SVG_GLYPH_REF_TAG, "", SVG_DY_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, SVG_FE_OFFSET_TAG, "", SVG_DX_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, SVG_FE_OFFSET_TAG, "", SVG_DY_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_EDGE_MODE_ATTRIBUTE, TYPE_EDGE_MODE_VALUE);
        putAttributeType(ns, any, "", SVG_ELEVATION_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_ENABLE_BACKGROUND_ATTRIBUTE, TYPE_ENABLE_BACKGROUND_VALUE);
        putAttributeType(ns, any, "", "end", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_EXPONENT_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_EXTERNAL_RESOURCES_REQUIRED_ATTRIBUTE, TYPE_BOOLEAN);
        putAttributeType(ns, "animate", "", SVG_FILL_ATTRIBUTE, TYPE_FILL_VALUE);
        putAttributeType(ns, "set", "", SVG_FILL_ATTRIBUTE, TYPE_FILL_VALUE);
        putAttributeType(ns, "animateMotion", "", SVG_FILL_ATTRIBUTE, TYPE_FILL_VALUE);
        putAttributeType(ns, "animateColor", "", SVG_FILL_ATTRIBUTE, TYPE_FILL_VALUE);
        putAttributeType(ns, "animateTransform", "", SVG_FILL_ATTRIBUTE, TYPE_FILL_VALUE);
        putAttributeType(ns, any, "", SVG_FILL_ATTRIBUTE, TYPE_PAINT);
        putAttributeType(ns, any, "", SVG_FILL_OPACITY_ATTRIBUTE, TYPE_OPACITY_VALUE);
        putAttributeType(ns, any, "", SVG_FILL_RULE_ATTRIBUTE, TYPE_CLIP_FILL_RULE);
        putAttributeType(ns, any, "", SVG_FILTER_ATTRIBUTE, TYPE_OPTIONAL_URI);
        putAttributeType(ns, any, "", SVG_FILTER_RES_ATTRIBUTE, TYPE_NUMBER_OPTIONAL_NUMBER);
        putAttributeType(ns, any, "", SVG_FILTER_UNITS_ATTRIBUTE, TYPE_UNITS_VALUE);
        putAttributeType(ns, any, "", SVG_FLOOD_COLOR_ATTRIBUTE, TYPE_SVG_COLOR);
        putAttributeType(ns, any, "", SVG_FLOOD_OPACITY_ATTRIBUTE, TYPE_OPACITY_VALUE);
        putAttributeType(ns, SVG_FONT_FACE_TAG, "", SVG_FONT_FAMILY_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_FONT_FAMILY_ATTRIBUTE, TYPE_FONT_FAMILY_VALUE);
        putAttributeType(ns, SVG_FONT_FACE_TAG, "", SVG_FONT_SIZE_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_FONT_SIZE_ATTRIBUTE, TYPE_FONT_SIZE_VALUE);
        putAttributeType(ns, any, "", "font-size-adjust", TYPE_FONT_SIZE_ADJUST_VALUE);
        putAttributeType(ns, SVG_FONT_FACE_TAG, "", SVG_FONT_STRETCH_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_FONT_STRETCH_ATTRIBUTE, TYPE_FONT_STRETCH_VALUE);
        putAttributeType(ns, SVG_FONT_FACE_TAG, "", SVG_FONT_STYLE_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_FONT_STYLE_ATTRIBUTE, TYPE_FONT_STYLE_VALUE);
        putAttributeType(ns, SVG_FONT_FACE_TAG, "", SVG_FONT_VARIANT_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_FONT_VARIANT_ATTRIBUTE, TYPE_FONT_VARIANT_VALUE);
        putAttributeType(ns, SVG_FONT_FACE_TAG, "", SVG_FONT_WEIGHT_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_FONT_WEIGHT_ATTRIBUTE, TYPE_FONT_WEIGHT_VALUE);
        putAttributeType(ns, any, "", "format", TYPE_STRING);
        putAttributeType(ns, any, "", "from", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_FX_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_FY_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_G1_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_G2_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_GLYPH_NAME_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", "glyph-orientation-horizontal", TYPE_GLYPH_ORIENTATION_HORIZONTAL_VALUE);
        putAttributeType(ns, any, "", "glyph-orientation-vertical", TYPE_GLYPH_ORIENTATION_VERTICAL_VALUE);
        putAttributeType(ns, any, "", SVG_GLYPH_REF_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_GRADIENT_TRANSFORM_ATTRIBUTE, TYPE_TRANSFORM_LIST);
        putAttributeType(ns, any, "", SVG_GRADIENT_UNITS_ATTRIBUTE, TYPE_UNITS_VALUE);
        putAttributeType(ns, any, "", SVG_HANGING_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_HEIGHT_ATTRIBUTE, TYPE_LENGTH);
        putAttributeType(ns, any, "", SVG_HORIZ_ADV_X_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_HORIZ_ORIGIN_X_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_HORIZ_ORIGIN_Y_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_IDEOGRAPHIC_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_IMAGE_RENDERING_ATTRIBUTE, TYPE_COLOR_IMAGE_RENDERING_VALUE);
        putAttributeType(ns, any, "", SVG_IN_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_IN2_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_INTERCEPT_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_K_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_K1_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_K2_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_K3_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_K4_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_KERNEL_MATRIX_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_KERNEL_UNIT_LENGTH_ATTRIBUTE, TYPE_NUMBER_OPTIONAL_NUMBER);
        putAttributeType(ns, any, "", SVG_KERNING_ATTRIBUTE, TYPE_KERNING_VALUE);
        putAttributeType(ns, any, "", "keyPoints", TYPE_STRING);
        putAttributeType(ns, any, "", "keySplines", TYPE_STRING);
        putAttributeType(ns, any, "", "keyTimes", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_LANG_ATTRIBUTE, TYPE_LANGUAGE_CODES);
        putAttributeType(ns, any, "", SVG_LENGTH_ADJUST_ATTRIBUTE, TYPE_LENGTH_ADJUST_VALUE);
        putAttributeType(ns, any, "", "letter-spacing", TYPE_SPACING_VALUE);
        putAttributeType(ns, any, "", "lighting-color", TYPE_SVG_COLOR);
        putAttributeType(ns, any, "", SVG_LIMITING_CONE_ANGLE_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_LOCAL_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", "marker-end", TYPE_OPTIONAL_URI);
        putAttributeType(ns, any, "", "marker-mid", TYPE_OPTIONAL_URI);
        putAttributeType(ns, any, "", "marker-start", TYPE_OPTIONAL_URI);
        putAttributeType(ns, any, "", SVG_MARKER_HEIGHT_ATTRIBUTE, TYPE_LENGTH);
        putAttributeType(ns, any, "", SVG_MARKER_UNITS_ATTRIBUTE, TYPE_MARKER_UNITS_VALUE);
        putAttributeType(ns, any, "", SVG_MARKER_WIDTH_ATTRIBUTE, TYPE_LENGTH);
        putAttributeType(ns, any, "", "mask", TYPE_OPTIONAL_URI);
        putAttributeType(ns, any, "", SVG_MASK_CONTENT_UNITS_ATTRIBUTE, TYPE_UNITS_VALUE);
        putAttributeType(ns, any, "", SVG_MASK_UNITS_ATTRIBUTE, TYPE_UNITS_VALUE);
        putAttributeType(ns, any, "", SVG_MATHEMATICAL_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", "max", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_MEDIA_ATTRIBUTE, TYPE_MEDIA_DESC);
        putAttributeType(ns, any, "", SVG_METHOD_ATTRIBUTE, TYPE_METHOD_VALUE);
        putAttributeType(ns, any, "", SVG_MODE_ATTRIBUTE, TYPE_MODE_VALUE);
        putAttributeType(ns, any, "", "min", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_NAME_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_NUM_OCTAVES_ATTRIBUTE, TYPE_INTEGER);
        putAttributeType(ns, SVG_STOP_TAG, "", SVG_OFFSET_ATTRIBUTE, TYPE_NUMBER_OR_PERCENTAGE);
        putAttributeType(ns, any, "", SVG_OFFSET_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", "onabort", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onactivate", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onbegin", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onclick", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onend", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onerror", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onfocusin", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onfocusout", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onload", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onmousedown", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onmousemove", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onmouseout", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onmouseover", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onmouseup", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onrepeat", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onresize", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onscroll", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onunload", TYPE_SCRIPT);
        putAttributeType(ns, any, "", "onzoom", TYPE_SCRIPT);
        putAttributeType(ns, any, "", SVG_OPACITY_ATTRIBUTE, TYPE_OPACITY_VALUE);
        putAttributeType(ns, SVG_FE_MORPHOLOGY_TAG, "", SVG_OPERATOR_ATTRIBUTE, TYPE_MORPHOLOGY_OPERATOR_VALUE);
        putAttributeType(ns, SVG_FE_COMPOSITE_TAG, "", SVG_OPERATOR_ATTRIBUTE, TYPE_COMPOSITE_OPERATOR_VALUE);
        putAttributeType(ns, any, "", SVG_ORDER_ATTRIBUTE, TYPE_NUMBER_OPTIONAL_NUMBER);
        putAttributeType(ns, any, "", SVG_ORIENT_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_ORIENTATION_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", "origin", TYPE_STRING);
        putAttributeType(ns, any, "", "overflow", TYPE_OVERFLOW_VALUE);
        putAttributeType(ns, any, "", SVG_OVERLINE_POSITION_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_OVERLINE_THICKNESS_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_PANOSE_1_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", "path", TYPE_STRING);
        putAttributeType(ns, any, "", "pathLength", TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_PATTERN_CONTENT_UNITS_ATTRIBUTE, TYPE_UNITS_VALUE);
        putAttributeType(ns, any, "", SVG_PATTERN_TRANSFORM_ATTRIBUTE, TYPE_TRANSFORM_LIST);
        putAttributeType(ns, any, "", SVG_PATTERN_UNITS_ATTRIBUTE, TYPE_UNITS_VALUE);
        putAttributeType(ns, any, "", "pointer-events", TYPE_POINTER_EVENTS_VALUE);
        putAttributeType(ns, any, "", SVG_POINTS_ATTRIBUTE, TYPE_POINTS);
        putAttributeType(ns, any, "", SVG_POINTS_AT_X_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_POINTS_AT_Y_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_POINTS_AT_Z_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_PRESERVE_ALPHA_ATTRIBUTE, TYPE_BOOLEAN);
        putAttributeType(ns, any, "", SVG_PRESERVE_ASPECT_RATIO_ATTRIBUTE, TYPE_PRESERVE_ASPECT_RATIO_SPEC);
        putAttributeType(ns, any, "", SVG_PRIMITIVE_UNITS_ATTRIBUTE, TYPE_UNITS_VALUE);
        putAttributeType(ns, any, "", SVG_R_ATTRIBUTE, TYPE_LENGTH);
        putAttributeType(ns, any, "", SVG_RADIUS_ATTRIBUTE, TYPE_NUMBER_OPTIONAL_NUMBER);
        putAttributeType(ns, any, "", SVG_REF_X_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_REF_Y_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_RENDERING_INTENT_ATTRIBUTE, TYPE_RENDERING_INTENT_VALUE);
        putAttributeType(ns, any, "", "repeatCount", TYPE_STRING);
        putAttributeType(ns, any, "", "repeatDur", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_REQUIRED_EXTENSIONS_ATTRIBUTE, TYPE_EXTENSION_LIST);
        putAttributeType(ns, any, "", "restart", TYPE_RESTART_VALUE);
        putAttributeType(ns, any, "", SVG_RESULT_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, "animateMotion", "", SVG_ROTATE_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_ROTATE_ATTRIBUTE, TYPE_NUMBERS);
        putAttributeType(ns, any, "", SVG_RX_ATTRIBUTE, TYPE_LENGTH);
        putAttributeType(ns, any, "", SVG_RY_ATTRIBUTE, TYPE_LENGTH);
        putAttributeType(ns, any, "", SVG_SCALE_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_SEED_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_SHAPE_RENDERING_ATTRIBUTE, TYPE_SHAPE_RENDERING_VALUE);
        putAttributeType(ns, any, "", SVG_SLOPE_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_SPECULAR_CONSTANT_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_SPECULAR_EXPONENT_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_SPREAD_METHOD_ATTRIBUTE, TYPE_SPREAD_METHOD_VALUE);
        putAttributeType(ns, any, "", SVG_START_OFFSET_ATTRIBUTE, TYPE_LENGTH);
        putAttributeType(ns, any, "", SVG_STD_DEVIATION_ATTRIBUTE, TYPE_NUMBER_OPTIONAL_NUMBER);
        putAttributeType(ns, any, "", SVG_STEMH_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_STEMV_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_STITCH_TILES_ATTRIBUTE, TYPE_STITCH_TILES_VALUE);
        putAttributeType(ns, any, "", SVG_STOP_COLOR_ATTRIBUTE, TYPE_SVG_COLOR);
        putAttributeType(ns, any, "", SVG_STOP_OPACITY_ATTRIBUTE, TYPE_OPACITY_VALUE);
        putAttributeType(ns, any, "", SVG_STRIKETHROUGH_POSITION_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_STRIKETHROUGH_THICKNESS_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_STROKE_ATTRIBUTE, TYPE_PAINT);
        putAttributeType(ns, any, "", SVG_STROKE_DASHARRAY_ATTRIBUTE, TYPE_STROKE_DASHARRAY_VALUE);
        putAttributeType(ns, any, "", SVG_STROKE_DASHOFFSET_ATTRIBUTE, TYPE_LENGTH_OR_INHERIT);
        putAttributeType(ns, any, "", SVG_STROKE_LINECAP_ATTRIBUTE, TYPE_STROKE_LINECAP_VALUE);
        putAttributeType(ns, any, "", SVG_STROKE_LINEJOIN_ATTRIBUTE, TYPE_STROKE_LINEJOIN_VALUE);
        putAttributeType(ns, any, "", SVG_STROKE_MITERLIMIT_ATTRIBUTE, TYPE_STROKE_MITERLIMIT_VALUE);
        putAttributeType(ns, any, "", SVG_STROKE_OPACITY_ATTRIBUTE, TYPE_OPACITY_VALUE);
        putAttributeType(ns, any, "", SVG_STROKE_WIDTH_ATTRIBUTE, TYPE_LENGTH_OR_INHERIT);
        putAttributeType(ns, any, "", SVG_SURFACE_SCALE_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_SYSTEM_LANGUAGE_ATTRIBUTE, TYPE_LANGUAGE_CODES);
        putAttributeType(ns, any, "", SVG_TABLE_VALUES_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_TARGET_ATTRIBUTE, TYPE_LINK_TARGET);
        putAttributeType(ns, any, "", SVG_TARGET_X_ATTRIBUTE, TYPE_INTEGER);
        putAttributeType(ns, any, "", SVG_TARGET_Y_ATTRIBUTE, TYPE_INTEGER);
        putAttributeType(ns, any, "", SVG_TEXT_ANCHOR_ATTRIBUTE, TYPE_TEXT_ANCHOR_VALUE);
        putAttributeType(ns, any, "", "text-decoration", TYPE_TEXT_DECORATION_VALUE);
        putAttributeType(ns, any, "", SVG_TEXT_RENDERING_ATTRIBUTE, TYPE_TEXT_RENDERING_VALUE);
        putAttributeType(ns, any, "", SVG_TEXT_LENGTH_ATTRIBUTE, TYPE_LENGTH);
        putAttributeType(ns, any, "", SVG_TITLE_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", "to", TYPE_STRING);
        putAttributeType(ns, any, "", SVG_TRANSFORM_ATTRIBUTE, TYPE_TRANSFORM_LIST);
        putAttributeType(ns, SVG_STYLE_TAG, "", SVG_TYPE_ATTRIBUTE, TYPE_CONTENT_TYPE);
        putAttributeType(ns, SVG_SCRIPT_TAG, "", SVG_TYPE_ATTRIBUTE, TYPE_CONTENT_TYPE);
        putAttributeType(ns, SVG_FE_TURBULENCE_TAG, "", SVG_TYPE_ATTRIBUTE, TYPE_TURBULENCE_TYPE_VALUE);
        putAttributeType(ns, "animateTransform", "", SVG_TYPE_ATTRIBUTE, TYPE_ANIMATE_TRANSFORM_TYPE_VALUE);
        putAttributeType(ns, any, "", SVG_TYPE_ATTRIBUTE, TYPE_COMPONENT_TRANSFER_TYPE_VALUE);
        putAttributeType(ns, any, "", SVG_U1_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_U2_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_UNDERLINE_POSITION_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_UNDERLINE_THICKNESS_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_UNICODE_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", "unicode-bidi", TYPE_UNICODE_BIDI_VALUE);
        putAttributeType(ns, any, "", SVG_UNICODE_RANGE_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_UNITS_PER_EM_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_V_ALPHABETIC_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_V_HANGING_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_V_IDEOGRAPHIC_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_V_MATHEMATICAL_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_VALUES_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", SVG_VERSION_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_VERT_ADV_Y_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_VERT_ORIGIN_X_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_VERT_ORIGIN_Y_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_VIEW_BOX_ATTRIBUTE, TYPE_VIEW_BOX_SPEC);
        putAttributeType(ns, any, "", "viewTarget", TYPE_STRING);
        putAttributeType(ns, any, "", "visibility", TYPE_VISIBILITY_VALUE);
        putAttributeType(ns, any, "", SVG_WIDTH_ATTRIBUTE, TYPE_LENGTH);
        putAttributeType(ns, any, "", SVG_WIDTHS_ATTRIBUTE, TYPE_STRING);
        putAttributeType(ns, any, "", "word-spacing", TYPE_SPACING_VALUE);
        putAttributeType(ns, any, "", "writing-mode", TYPE_WRITING_MODE_VALUE);
        putAttributeType(ns, SVG_GLYPH_REF_TAG, "", SVG_X_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, SVG_FE_POINT_LIGHT_TAG, "", SVG_X_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, SVG_FE_SPOT_LIGHT_TAG, "", SVG_X_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, SVG_TEXT_TAG, "", SVG_X_ATTRIBUTE, TYPE_COORDINATE/*S*/);  // XXX temporary hack
        putAttributeType(ns, SVG_TSPAN_TAG, "", SVG_X_ATTRIBUTE, TYPE_COORDINATE/*S*/);
        putAttributeType(ns, SVG_TREF_TAG, "", SVG_X_ATTRIBUTE, TYPE_COORDINATE/*S*/);
        putAttributeType(ns, SVG_ALT_GLYPH_TAG, "", SVG_X_ATTRIBUTE, TYPE_COORDINATE/*S*/);
        putAttributeType(ns, any, "", SVG_X_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_X1_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_X2_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_X_CHANNEL_SELECTOR_ATTRIBUTE, TYPE_CHANNEL_SELECTOR_VALUE);
        putAttributeType(ns, SVG_GLYPH_REF_TAG, "", SVG_Y_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, SVG_FE_POINT_LIGHT_TAG, "", SVG_Y_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, SVG_FE_SPOT_LIGHT_TAG, "", SVG_Y_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, SVG_TEXT_TAG, "", SVG_Y_ATTRIBUTE, TYPE_COORDINATE/*S*/);  // XXX temporary hack
        putAttributeType(ns, SVG_TSPAN_TAG, "", SVG_Y_ATTRIBUTE, TYPE_COORDINATE/*S*/);
        putAttributeType(ns, SVG_TREF_TAG, "", SVG_Y_ATTRIBUTE, TYPE_COORDINATE/*S*/);
        putAttributeType(ns, SVG_ALT_GLYPH_TAG, "", SVG_Y_ATTRIBUTE, TYPE_COORDINATE/*S*/);
        putAttributeType(ns, any, "", SVG_Y_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_Y1_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_Y2_ATTRIBUTE, TYPE_COORDINATE);
        putAttributeType(ns, any, "", SVG_Y_CHANNEL_SELECTOR_ATTRIBUTE, TYPE_CHANNEL_SELECTOR_VALUE);
        putAttributeType(ns, any, "", SVG_Z_ATTRIBUTE, TYPE_NUMBER);
        putAttributeType(ns, any, "", SVG_ZOOM_AND_PAN_ATTRIBUTE, TYPE_ZOOM_AND_PAN_VALUE);
        putAttributeType(any, any, "http://www.w3.org/1999/xlink", "href", TYPE_URI);
        putAttributeType(org.apache.batik.constraint.Constants.CSVG_NAMESPACE_URI,
                         "variable",
                         "",
                         "value",
                         TYPE_UNKNOWN);
    }
    
    /**
     * Get the type of the specified attribute on a particularly named element.
     */
	public static int getAttributeType(String elementNamespace,
                                       String elementLocalName, 
                                       String attributeNamespace, 
                                       String attributeLocalName) {

        String any = "*";
        if (elementNamespace == null) {
            elementNamespace = "";
        }
        if (attributeNamespace == null) {
            attributeNamespace = "";
        }

        Integer i = (Integer) attributeTypeTable.get(
                        elementNamespace + ":" + elementLocalName,
                        attributeNamespace + ":" + attributeLocalName);
        if (i != null) {
            return i.intValue();
        }

        i = (Integer) attributeTypeTable.get(
                        elementNamespace + ":*",
                        attributeNamespace + ":" + attributeLocalName);
        if (i != null) {
            return i.intValue();
        }

        i = (Integer) attributeTypeTable.get(
                        "*:*",
                        attributeNamespace + ":" + attributeLocalName);
        if (i != null) {
            return i.intValue();
        }

		// return TYPE_UNKNOWN;
		return TYPE_STRING;
	}

    /**
     * Throw an exception indicating the given string could
     * not be converted to the given type.
     */
    protected static void invalidStringConversion(String value, int toType)
            throws TypeErrorException {
        throw new TypeErrorException("Cannot convert String \"" + value
            + "\" to " + TYPENAMES[toType] + ".");
    }

    /**
     * Throw an exception indicating a type error.
     */
    protected static void invalidConversion(int fromType, int toType)
            throws TypeErrorException {
        throw new TypeErrorException("Cannot convert " + TYPENAMES[fromType] 
            + " to " + TYPENAMES[toType] + ".");
    }
    
    /**
     * Convert this value to another type, if possible.
     */
    public Value convertTo(int type) throws TypeErrorException {
        return convertTo(type, null);
    }

    /**
     * Convert this value to another type specifying an origin, if possible.
     */
    public abstract Value convertTo(int type, Node origin)
            throws TypeErrorException;

    /**
     * Get the name of this type of value.
     */
    public String getTypeName() {
        return TYPENAMES[type];
    }

    /**
     * Convert the given Attr to a Value.
     */
    public static Value normaliseAttr(Attr a) {
        Element e = a.getOwnerElement();
        String an = a.getName();
        String prefix = DOMUtilities.getPrefix(an);
        String aln = DOMUtilities.getLocalName(an);
        String ans;
        if (prefix == null) {
            ans = null;
        } else {
            PrefixResolver pr = new PrefixResolver(e);
            ans = pr.getNamespaceForPrefix(prefix);
        }
        String eln = e.getLocalName();
        String ens = e.getNamespaceURI();
        SVGOMDocument doc = (SVGOMDocument) a.getOwnerDocument();
        ConstraintEngine ce = doc.getConstraintEngine();
        Constraint c = ce.getConstraint(e, ans, aln);
        // System.out.println("normaliseAttr(" + eln + "/@" + aln + ")");
        // System.out.println("  c = " + c);
        if (c != null) {
            return c.getValue();
        }
        int attributeType = getAttributeType(ens, eln, ans, aln);
        ConstraintsAnimationEngine ae = doc.getAnimationEngine();
        List al = ae.getAnimations(e, aln);
        if (al != null) {
            Iterator i = al.iterator();
            while (i.hasNext()) {
                Animation ani = (Animation) i.next();
                String v = ani.getValue();
                if (v != null) {
                    return createValue(attributeType, v, ani.getElement());
                }
            }
        }
        return createValue(/*ce, */attributeType, a.getValue(), a);
    }

    /**
     * Convert an XNodeSet into an XExtensibleObject wrapping the
     * item in the nodeset if necessary.
     */
    public static XObject normaliseXObject(XObject xo) {
        try {
            if (xo.getType() == XObject.CLASS_NODESET) {
                NodeList nl = xo.nodelist();
                if (nl.getLength() > 0) {
                    Node n = nl.item(0);
                    if (n.getNodeType() == Node.ATTRIBUTE_NODE) {
                        return new XConstraintObject(normaliseAttr((Attr) n));
                    }
                }
            }
        } catch (javax.xml.transform.TransformerException te) {
        }
        return xo;
    }

    /**
     * Create a Value object based on an attributeType and a string.
     */
    public static Value createValue(/*ConstraintEngine ce,*/
                                    int attributeType, 
                                    String s, 
                                    Node origin) {
        switch (attributeType) {
            case TYPE_STRING:
                return new StringValue(/*ce,*/ s);
            case TYPE_BOOLEAN:
                return new BooleanValue(/*ce,*/ s);
            case TYPE_INTEGER:
                return new IntegerValue(/*ce,*/ s);
            case TYPE_NUMBER:
                return new NumberValue(/*ce,*/ s);
            case TYPE_LENGTH:
                return new LengthValue(/*ce,*/ origin, s);
            case TYPE_COORDINATE:
                return new CoordinateValue(/*ce,*/ origin, s);
            case TYPE_URI:
                return new URIValue(/*ce,*/ s);
            case TYPE_MATRIX:
                return new MatrixValue(/*ce,*/ s);
            case TYPE_NUMBER_OR_PERCENTAGE:
                return new NumberOrPercentageValue(/*ce,*/ s);
            case TYPE_POINT:
                return new PointValue(/*ce,*/ s);
            case TYPE_RECT:
                return new RectValue(/*ce,*/ s);
        }
        return new UnconvertibleValue(/*ce,*/ s, attributeType);
    }

    /**
     * Create a Value from an XObject.
     */
    public static Value createValue(/*ConstraintEngine ce,*/ XObject xo) {
        try {
            xo = normaliseXObject(xo);
            switch (xo.getType()) {
                case XObject.CLASS_NULL:
                    return null;
                case XObject.CLASS_UNKNOWN:
                    Object o = xo.object();
                    if (o == null) {
                        return null;
                    }
                    if (o instanceof Value) {
                        return (Value) o;
                    }
                    throw new TypeErrorException("Unknown object type "
                            + o.getClass().getName() + " in XPath expression.");
                case XObject.CLASS_BOOLEAN:
                    return new BooleanValue(/*ce,*/ xo.bool());
                case XObject.CLASS_NUMBER:
                    return new NumberValue(/*ce,*/ (float) xo.num());
                case XObject.CLASS_STRING:
                    return new StringValue(/*ce,*/ xo.str());
                case XObject.CLASS_NODESET:
                    // XXX is this the right thing to do?  maybe should keep
                    //     the nodeset around for longer.
                    return new StringValue(xo.str());
                    // throw new TypeErrorException("Cannot handle nodeset.");
            }
            throw new TypeErrorException("Unknown object type "
                    + xo.getTypeString() + " in XPath expression.");
        } catch (javax.xml.transform.TransformerException te) {
            throw new XPathException
                ("Could not create Value object from XPath object: "
                    + te.getMessage(), te);
        }
    }
    
    /**
     * Test if two Value objects are equivalent.
     */
    public abstract boolean equals(Value v);
    
    // Operators /////////////////////////////////////////////////////////////

    public XObject notequals(XObject r) throws TransformerException { return null; }
    public XObject notequalsRev(XObject l) throws TransformerException { return null; }
    public XObject equals(XObject r) throws TransformerException { return null; }
    public XObject equalsRev(XObject l) throws TransformerException { return null; }
    public XObject lte(XObject r) throws TransformerException { return null; }
    public XObject lteRev(XObject l) throws TransformerException { return null; }
    public XObject lt(XObject r) throws TransformerException { return null; }
    public XObject ltRev(XObject l) throws TransformerException { return null; }
    public XObject gte(XObject r) throws TransformerException { return null; }
    public XObject gteRev(XObject l) throws TransformerException { return null; }
    public XObject gt(XObject r) throws TransformerException { return null; }
    public XObject gtRev(XObject l) throws TransformerException { return null; }
    public XObject plus(XObject r) throws TransformerException { return null; }
    public XObject plusRev(XObject l) throws TransformerException { return null; }
    public XObject minus(XObject r) throws TransformerException { return null; }
    public XObject minusRev(XObject l) throws TransformerException { return null; }
    public XObject mult(XObject r) throws TransformerException { return null; }
    public XObject multRev(XObject l) throws TransformerException { return null; }
    public XObject div(XObject r) throws TransformerException { return null; }
    public XObject divRev(XObject l) throws TransformerException { return null; }
    public XObject mod(XObject r) throws TransformerException { return null; }
    public XObject modRev(XObject l) throws TransformerException { return null; }
    public XObject neg() throws TransformerException { return null; }
    public XObject string() throws TransformerException { return null; }
    public XObject boolean_() throws TransformerException { return null; }
    public XObject number() throws TransformerException { return null; }
}
