package felix_h.de.increaseyourlifetime;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MyAdapter extends BaseAdapter
{
    private Context context;
    private ListBean[] mDatas;

    public MyAdapter(Context context, ListBean[] mDatas) {
        this.context = context;
        this.mDatas = mDatas;
    }

    @Override
    public int getCount() {
        return mDatas.length;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView==null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_menu,null);
            holder.tv = (TextView) convertView.findViewById(R.id.tv_text);
            holder.iv = (ImageView) convertView.findViewById(R.id.iv_image);
            convertView.setTag(holder);
        }
        else{
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv.setText(mDatas[position].getTitle());
        holder.iv.setImageResource(mDatas[position].getResource());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(context);
                LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View mView = null;
                switch (position) {
                    case 0:
                        Intent i = new Intent(context, EditUserData.class);
                        context.startActivity(i);
                        break;
                    case 1:
                        context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + context.getPackageName())));
                        break;
                    case 2:
                        mView = inflater.inflate(R.layout.dialog_licenses, null);
                        break;
                    case 3:
                        String url = "https://app.felix-h.de/privacy-policy/";
                        Intent in = new Intent(Intent.ACTION_VIEW);
                        in.setData(Uri.parse(url));
                        context.startActivity(in);
                        break;
                    case 4:
                        /* Create the Intent */
                        final Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
                        /* Fill it with Data */
                        emailIntent.setType("plain/text");
                        emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, new String[]{"app@felix-h.de"});
                        emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "");
                        emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, "");

                        /* Send it off to the Activity-Chooser */
                        context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
                        break;
                    case 5:
                        mView = inflater.inflate(R.layout.dialog_about, null);
                        break;
                    default:
                        break;
                }
                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
                dialog.show();
                }
    });
        return convertView;
    }

    static class ViewHolder{
        TextView tv;
        ImageView iv;
    }
}
