package com.ye_sk.maploadertask;

import android.content.Context;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.LinkedList;

public enum RegionController {
    INSTANCE;

    private LinkedList<Region> regions = new LinkedList<>();
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public void loadRegions(){
        StringBuilder tmp;
        try {
            XmlPullParser xpp = context.getResources().getXml(R.xml.regions);
            while (xpp.getEventType() != XmlPullParser.END_DOCUMENT) {
                switch (xpp.getEventType()) {
                    case XmlPullParser.START_DOCUMENT:
                        System.out.println( "START_DOCUMENT");
                        break;
                    case XmlPullParser.START_TAG:
                        System.out.println("START_TAG: name = " + xpp.getName()
                                + ", depth = " + xpp.getDepth() + ", attrCount = "
                                + xpp.getAttributeCount());
                        tmp = new StringBuilder();
                        for (int i = 0; i < xpp.getAttributeCount(); i++) {
                            tmp.append(xpp.getAttributeName(i)).append(" = ").append(xpp.getAttributeValue(i)).append("& ");
                        }
                        if (!TextUtils.isEmpty(tmp.toString())) {
                            System.out.println("Attributes: " + tmp.toString());
                            if(xpp.getDepth()>0)
                                getLastElementByDepth(xpp.getDepth()-2, regions).add(parseRegion(tmp.toString()));
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        System.out.println( "END_TAG: name = " + xpp.getName());
                        break;
/*                    case XmlPullParser.TEXT:
                        //System.out.println( "text = " + xpp.getText());
                        break;*/

                    default:
                        break;
                }
                xpp.next();
            }
            System.out.println( "END_DOCUMENT");

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Region parseRegion(String attributes){
        Region region = new Region();
        String[] arr = attributes.split("& ");
        String[] couple;
        for(String atr:arr){
            couple = atr.split("= ");
            if(couple.length<2)
                continue;
            region.setMeaning(couple[0].replace(" ",""),couple[1]);
        }
        return region;
    }

    private LinkedList<Region> getLastElementByDepth(int depth, LinkedList<Region> list){
        return depth==0?list:getLastElementByDepth(depth-1, list.getLast().getSubRegions());
    }
}
