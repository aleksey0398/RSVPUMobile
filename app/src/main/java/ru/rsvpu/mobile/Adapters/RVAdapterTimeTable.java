package ru.rsvpu.mobile.Adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.rsvpu.mobile.R;
import ru.rsvpu.mobile.items.DateUtil;
import ru.rsvpu.mobile.items.TimeTableOneDay;
import ru.rsvpu.mobile.items.TimeTableOneLesson;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

/**
 * Created by aleksej on 13.10.2017.
 */

public class RVAdapterTimeTable extends RecyclerView.Adapter<RVAdapterTimeTable.TimeTableViewHolder> {

    private Context context;
    private List<TimeTableOneDay> lessons = new ArrayList<>();
    private final String LOG_ARGS = "TimeTableAdapter";

    public RVAdapterTimeTable(Context context) {
        this.context = context;
    }

    public void setList(List<TimeTableOneDay> list) {
        this.lessons = list;
        notifyDataSetChanged();
    }


    @Override
    public TimeTableViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.content_timetable, parent, false);

        return new TimeTableViewHolder(view);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(TimeTableViewHolder holder, int position) {
        TimeTableOneDay timeTable = lessons.get(position);
        holder.txt_date.setText(timeTable.dayOfTheWeek + " " + timeTable.date);
        Log.d(LOG_ARGS,DateUtil.generateToday());
        if(DateUtil.generateToday().equals(timeTable.date)){
            holder.txt_date.setTextColor(context.getResources().getColor(R.color.todayColor));
        } else {
            holder.txt_date.setTextColor(context.getResources().getColor(android.R.color.white));
        }

        holder.lesson1.setVisibility(VISIBLE);
        holder.lesson2.setVisibility(VISIBLE);
        holder.lesson3.setVisibility(VISIBLE);
        holder.lesson4.setVisibility(VISIBLE);
        holder.lesson5.setVisibility(VISIBLE);
        holder.lesson6.setVisibility(VISIBLE);
        holder.lesson7.setVisibility(VISIBLE);

//        System.out.println("\n\n");
//        System.out.println(timeTable.dayOfTheWeek + " " + timeTable.date);

        for (int i = 0; i < timeTable.lessons.length; i++) {
            TimeTableOneLesson lesson = timeTable.lessons[i];

            holder.txt_time_first[i].setText(lesson.timeStart);

            if (lesson.lessonsName.get(0).equals("-")) {
                if (i == 0) {
                    holder.lesson1.setVisibility(GONE);
                }
                if (i == 1) {
                    holder.lesson2.setVisibility(GONE);
                }
                if (i == 2) {
                    holder.lesson3.setVisibility(GONE);
                }
                if (i == 3) {
                    holder.lesson4.setVisibility(GONE);
                }
                if (i == 4) {
                    holder.lesson5.setVisibility(GONE);
                }
                if (i == 5) {
                    holder.lesson6.setVisibility(GONE);
                }
                if (i == 6) {
                    holder.lesson7.setVisibility(GONE);
                }
                continue;
            }

            if (lesson.lessonsName.size() < 2) {

                holder.txt_type[i * 2].setText(lesson.typeOfLesson.get(0));
                holder.txt_title[i * 2].setText(lesson.lessonsName.get(0));

                String teacherString = "";
                for (TimeTableOneLesson.Tuple teacher : lesson.teachers) {
                    teacherString += teacher.name + ", ";
                }
                if (teacherString.length() != 0)
                    holder.txt_teacher[i * 2].setText(teacherString.substring(0, (teacherString.length() - 2)));

                String classString = "";
                for (TimeTableOneLesson.Tuple classes : lesson.classrooms) {
                    classString += classes.name + ", ";
                }
                if (classString.length() != 0)
                    holder.txt_class[i * 2].setText(classString.substring(0, (classString.length() - 2)));

                if (lesson.numberOfGroup.size() > 0 && lesson.numberOfGroup.get(0) != null) {
                    holder.txt_class[i * 2].setText(holder.txt_class[i * 2].getText() + " " + lesson.numberOfGroup.get(0));
                }

                holder.second_linear_layout[i].setVisibility(GONE);
            } else {
                holder.txt_type[i * 2].setText(lesson.typeOfLesson.get(0));
                holder.txt_type[i * 2 + 1].setText(lesson.typeOfLesson.get(1));
                System.out.println(lesson.lessonsName.size());
                holder.second_linear_layout[i].setVisibility(VISIBLE);

                holder.txt_class[i*2].setText(lesson.classrooms.get(0).name);
                holder.txt_class[i*2+1].setText(lesson.classrooms.get(1).name);

                holder.txt_teacher[i*2].setText(lesson.teachers.get(0).name);
                holder.txt_teacher[i*2+1].setText(lesson.teachers.get(1).name);

                holder.txt_title[i*2].setText(lesson.lessonsName.get(0));
                holder.txt_title[i*2+1].setText(lesson.lessonsName.get(1));

                holder.txt_class[i*2].setText(holder.txt_class[i*2].getText()+" "+ lesson.numberOfGroup.get(0));
                holder.txt_class[i*2+1].setText(holder.txt_class[i*2+1].getText()+" "+ lesson.numberOfGroup.get(1));

            }
//            System.out.println(lesson.lessonsName.size());
        }

    }

    @Override
    public int getItemCount() {
        return lessons.size();
    }

    class TimeTableViewHolder extends RecyclerView.ViewHolder {
        private TextView txt_date;
        private View lesson1, lesson2, lesson3, lesson4, lesson5, lesson6, lesson7;
        private TextView[] txt_type = new TextView[14];
        private TextView[] txt_class = new TextView[14];
        private TextView[] txt_teacher = new TextView[14];
        private TextView[] txt_title = new TextView[14];
        private TextView[] txt_time_first = new TextView[7];
        private TextView[] txt_time_second = new TextView[7];
        private LinearLayout second_linear_layout[] = new LinearLayout[7];

        TimeTableViewHolder(View itemView) {
            super(itemView);
            txt_date = itemView.findViewById(R.id.tt_date);

            lesson1 = itemView.findViewById(R.id.lesson1);
            txt_type[0] = lesson1.findViewById(R.id.lesson_txt_type);
            txt_type[1] = lesson1.findViewById(R.id.lesson_txt_type2);
            second_linear_layout[0] = lesson1.findViewById(R.id.lesson_second_layout);
            txt_class[0] = lesson1.findViewById(R.id.lesson_txt_class);
            txt_class[1] = lesson1.findViewById(R.id.lesson_txt_class2);
            txt_teacher[0] = lesson1.findViewById(R.id.lesson_txt_teacher);
            txt_teacher[1] = lesson1.findViewById(R.id.lesson_txt_teacher2);
            txt_title[0] = lesson1.findViewById(R.id.lesson_txt_title);
            txt_title[1] = lesson1.findViewById(R.id.lesson_txt_title2);
            txt_time_first[0] = lesson1.findViewById(R.id.lesson_txt_time_first);
            txt_time_second[0] = lesson1.findViewById(R.id.lesson_txt_time_second);

            lesson2 = itemView.findViewById(R.id.lesson2);
            txt_type[2] = lesson2.findViewById(R.id.lesson_txt_type);
            txt_type[3] = lesson2.findViewById(R.id.lesson_txt_type2);
            second_linear_layout[1] = lesson2.findViewById(R.id.lesson_second_layout);
            txt_class[2] = lesson2.findViewById(R.id.lesson_txt_class);
            txt_class[3] = lesson2.findViewById(R.id.lesson_txt_class2);
            txt_teacher[2] = lesson2.findViewById(R.id.lesson_txt_teacher);
            txt_teacher[3] = lesson2.findViewById(R.id.lesson_txt_teacher2);
            txt_title[2] = lesson2.findViewById(R.id.lesson_txt_title);
            txt_title[3] = lesson2.findViewById(R.id.lesson_txt_title2);
            txt_time_first[1] = lesson2.findViewById(R.id.lesson_txt_time_first);
            txt_time_second[1] = lesson2.findViewById(R.id.lesson_txt_time_second);

            lesson3 = itemView.findViewById(R.id.lesson3);
            txt_type[4] = lesson3.findViewById(R.id.lesson_txt_type);
            txt_type[5] = lesson3.findViewById(R.id.lesson_txt_type2);
            second_linear_layout[2] = lesson3.findViewById(R.id.lesson_second_layout);
            txt_class[4] = lesson3.findViewById(R.id.lesson_txt_class);
            txt_class[5] = lesson3.findViewById(R.id.lesson_txt_class2);
            txt_teacher[4] = lesson3.findViewById(R.id.lesson_txt_teacher);
            txt_teacher[5] = lesson3.findViewById(R.id.lesson_txt_teacher2);
            txt_title[4] = lesson3.findViewById(R.id.lesson_txt_title);
            txt_title[5] = lesson3.findViewById(R.id.lesson_txt_title2);
            txt_time_first[2] = lesson3.findViewById(R.id.lesson_txt_time_first);
            txt_time_second[2] = lesson3.findViewById(R.id.lesson_txt_time_second);

            lesson4 = itemView.findViewById(R.id.lesson4);
            txt_type[6] = lesson4.findViewById(R.id.lesson_txt_type);
            txt_type[7] = lesson4.findViewById(R.id.lesson_txt_type2);
            second_linear_layout[3] = lesson4.findViewById(R.id.lesson_second_layout);
            txt_class[6] = lesson4.findViewById(R.id.lesson_txt_class);
            txt_class[7] = lesson4.findViewById(R.id.lesson_txt_class2);
            txt_teacher[6] = lesson4.findViewById(R.id.lesson_txt_teacher);
            txt_teacher[7] = lesson4.findViewById(R.id.lesson_txt_teacher2);
            txt_title[6] = lesson4.findViewById(R.id.lesson_txt_title);
            txt_title[7] = lesson4.findViewById(R.id.lesson_txt_title2);
            txt_time_first[3] = lesson4.findViewById(R.id.lesson_txt_time_first);
            txt_time_second[3] = lesson4.findViewById(R.id.lesson_txt_time_second);

            lesson5 = itemView.findViewById(R.id.lesson5);
            txt_type[8] = lesson5.findViewById(R.id.lesson_txt_type);
            txt_type[9] = lesson5.findViewById(R.id.lesson_txt_type2);
            second_linear_layout[4] = lesson5.findViewById(R.id.lesson_second_layout);
            txt_class[8] = lesson5.findViewById(R.id.lesson_txt_class);
            txt_class[9] = lesson5.findViewById(R.id.lesson_txt_class2);
            txt_teacher[8] = lesson5.findViewById(R.id.lesson_txt_teacher);
            txt_teacher[9] = lesson5.findViewById(R.id.lesson_txt_teacher2);
            txt_title[8] = lesson5.findViewById(R.id.lesson_txt_title);
            txt_title[9] = lesson5.findViewById(R.id.lesson_txt_title2);
            txt_time_first[4] = lesson5.findViewById(R.id.lesson_txt_time_first);
            txt_time_second[4] = lesson5.findViewById(R.id.lesson_txt_time_second);

            lesson6 = itemView.findViewById(R.id.lesson6);
            txt_type[10] = lesson6.findViewById(R.id.lesson_txt_type);
            txt_type[11] = lesson6.findViewById(R.id.lesson_txt_type2);
            second_linear_layout[5] = lesson6.findViewById(R.id.lesson_second_layout);
            txt_class[10] = lesson6.findViewById(R.id.lesson_txt_class);
            txt_class[11] = lesson6.findViewById(R.id.lesson_txt_class2);
            txt_teacher[10] = lesson6.findViewById(R.id.lesson_txt_teacher);
            txt_teacher[11] = lesson6.findViewById(R.id.lesson_txt_teacher2);
            txt_title[10] = lesson6.findViewById(R.id.lesson_txt_title);
            txt_title[11] = lesson6.findViewById(R.id.lesson_txt_title2);
            txt_time_first[5] = lesson6.findViewById(R.id.lesson_txt_time_first);
            txt_time_second[5] = lesson6.findViewById(R.id.lesson_txt_time_second);

            lesson7 = itemView.findViewById(R.id.lesson7);
            txt_type[12] = lesson7.findViewById(R.id.lesson_txt_type);
            txt_type[13] = lesson7.findViewById(R.id.lesson_txt_type2);
            second_linear_layout[6] = lesson7.findViewById(R.id.lesson_second_layout);
            txt_class[12] = lesson7.findViewById(R.id.lesson_txt_class);
            txt_class[13] = lesson7.findViewById(R.id.lesson_txt_class2);
            txt_teacher[12] = lesson7.findViewById(R.id.lesson_txt_teacher);
            txt_teacher[13] = lesson7.findViewById(R.id.lesson_txt_teacher2);
            txt_title[12] = lesson7.findViewById(R.id.lesson_txt_title);
            txt_title[13] = lesson7.findViewById(R.id.lesson_txt_title2);
            txt_time_first[6] = lesson7.findViewById(R.id.lesson_txt_time_first);
            txt_time_second[6] = lesson7.findViewById(R.id.lesson_txt_time_second);

        }
    }
}