package com.strukovsky.financiel.ui.analysis;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.strukovsky.financiel.R;
import com.strukovsky.financiel.calculations.Efficiency;
import com.strukovsky.financiel.calculations.Returns;
import com.strukovsky.financiel.db.entity.BalanceSheet;
import com.strukovsky.financiel.db.entity.CashFlow;
import com.strukovsky.financiel.db.entity.Share;
import com.strukovsky.financiel.db.task.FindAllDataByShareId;
import com.strukovsky.financiel.db.task.ShareDataBundle;
import com.strukovsky.financiel.db.task.TaskRunner;
import com.strukovsky.financiel.db.task.balance_sheet.FindBalanceSheetByShareIdTask;
import com.strukovsky.financiel.db.task.cash_flow.FindCashFlowByShareIdTask;
import com.strukovsky.financiel.db.task.share.FindShareByIdTask;

public class AnalysisFragment extends Fragment {

    private AnalysisViewModel analysisViewModel;
    TextView ticker;
    TextView industry;
    TextView name;
    TextView netIncomeToRevenue;
    TextView returnOnEquity;
    TextView returnOnAssets;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        analysisViewModel =
                new ViewModelProvider(this).get(AnalysisViewModel.class);
        View root = inflater.inflate(R.layout.fragment_analysis, container, false);
        ticker = root.findViewById(R.id.share_ticker);
        industry = root.findViewById(R.id.share_industry);
        name = root.findViewById(R.id.share_name);
        netIncomeToRevenue = root.findViewById(R.id.net_income_to_revenue);
        returnOnAssets = root.findViewById(R.id.return_on_assets);
        returnOnEquity = root.findViewById(R.id.return_on_equity);
        if (getArguments() == null)
        {

        }
        else performAnalysis();

        return root;
    }
    Share share;
    CashFlow cashFlow;
    BalanceSheet balanceSheet;
    private void performAnalysis()
    {
        prepareDataFromDB();
    }

    private void setShareInfo() {
        ticker.setText(share.getTicker());
        industry.setText(share.getIndustry());
        name.setText(share.getName());
    }

    private void makeAnalysisOfReturns() {
        returnOnEquity.setText(Returns.INSTANCE.returnOnEquity(balanceSheet, cashFlow));
        returnOnAssets.setText(Returns.INSTANCE.returnOnAssets(balanceSheet, cashFlow));
    }

    private void makeAnalysisOfEfficiency() {
        netIncomeToRevenue.setText(Efficiency.INSTANCE.netIncomeToRevenue(cashFlow));
    }

    private void prepareDataFromDB()
    {
        int id = getArguments().getInt("SHARE_ID");
        TaskRunner.INSTANCE.execute(new FindAllDataByShareId(requireContext(), id), new TaskRunner.Callback<ShareDataBundle>() {
            @Override
            public void onComplete(ShareDataBundle result) {
                share = result.getShare();
                cashFlow = result.getCashFlow();
                balanceSheet = result.getBalanceSheet();
                setShareInfo();
                makeAnalysisOfEfficiency();
                makeAnalysisOfReturns();
            }
        });
    }
}