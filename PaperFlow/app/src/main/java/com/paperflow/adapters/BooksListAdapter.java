package com.paperflow.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.paperflow.R;
import com.paperflow.responses.GetUserBooksResponse;

import java.util.ArrayList;

public class BooksListAdapter extends ArrayAdapter<GetUserBooksResponse.UserBook> implements Filterable {

    private ItemFilter mFilter = new ItemFilter();
    private Context mContext;
    RequestOptions requestOptions;
    private ArrayList<GetUserBooksResponse.UserBook> moviesList = new ArrayList<>();
    private ArrayList<GetUserBooksResponse.UserBook> filteredData = new ArrayList<>();

    public BooksListAdapter(Context context, ArrayList<GetUserBooksResponse.UserBook> list) {
        super(context, 0 , list);
        mContext = context;
        moviesList = list;
        filteredData = list;
        requestOptions = new RequestOptions();
        requestOptions.bitmapTransform(new RoundedCorners((int)context.getResources().getDimension(R.dimen.dp_8)));

    }
    private static class ViewHolder {
        TextView name,desc,price;
        ImageView image;

    }

    @Override
    public int getCount() {
        return filteredData.size();
    }

    @Override
    public GetUserBooksResponse.UserBook getItem(int position) {
        return filteredData.get(position);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        final ViewHolder viewHolder; // view lookup cache stored in tag
        GetUserBooksResponse.UserBook userBook = filteredData.get(position);

        if (convertView == null) {
            // If there's no view to re-use, inflate a brand new view for row
            viewHolder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.book_grid_list_item, parent,
                    false);

            viewHolder.image = (ImageView)convertView.findViewById(R.id.bookImageView);

            //viewHolder.image.setLayoutParams(new LinearLayout.LayoutParams(200,300));


            convertView.setTag(viewHolder);
        } else {
            // View is being recycled, retrieve the viewHolder object from tag
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.image.setAdjustViewBounds(true);

        Glide.with(mContext).load(userBook.getThumbnail()).apply(RequestOptions.bitmapTransform(new RoundedCorners(10))).into(viewHolder.image);

        return convertView;
    }


    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final ArrayList<GetUserBooksResponse.UserBook> list = moviesList;

            int count = list.size();
            final ArrayList<GetUserBooksResponse.UserBook> nlist = new ArrayList<GetUserBooksResponse.UserBook>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i).getBookname();
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(list.get(i));
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<GetUserBooksResponse.UserBook>) results.values;
            notifyDataSetChanged();
        }

    }

}
