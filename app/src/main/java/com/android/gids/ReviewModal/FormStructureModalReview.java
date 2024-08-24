package com.android.gids.ReviewModal;

import com.android.gids.ElementChoice;
import com.android.gids.ui.home.BranchinglogicModal;

import java.util.List;

public class FormStructureModalReview {


    public String id="";
    public String element_type="";
    public String repeat="";
    public String element_label="";
    public String select_type="";
    public String select_global_data_set_id="";
    public String display_order="";
    public String element_variable="";
    public String element_validation="";
    public String maximum="";
    public String minimum="";
    public String calculation_logic="";

    public String readonly="";

    public String element_required="";
    public String element_note="";

    public String interlink_question_id="";

    public List<ElementChoice> element_choices;

    public List<BranchinglogicModal> cause_branching_logic;

    public List<BranchinglogicModal> effect_branching_logic;


    public String vlookup="";

    public String vlookup_qustion_id="";


    public String status="";


    public String created_at="";

    public String updated_at="";


    public String answers ="";
    public String feedback ="";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getElement_type() {
        return element_type;
    }

    public void setElement_type(String element_type) {
        this.element_type = element_type;
    }

    public String getRepeat() {
        return repeat;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public String getElement_label() {
        return element_label;
    }

    public void setElement_label(String element_label) {
        this.element_label = element_label;
    }

    public String getSelect_type() {
        return select_type;
    }

    public void setSelect_type(String select_type) {
        this.select_type = select_type;
    }

    public String getSelect_global_data_set_id() {
        return select_global_data_set_id;
    }

    public void setSelect_global_data_set_id(String select_global_data_set_id) {
        this.select_global_data_set_id = select_global_data_set_id;
    }

    public String getDisplay_order() {
        return display_order;
    }

    public void setDisplay_order(String display_order) {
        this.display_order = display_order;
    }

    public String getElement_variable() {
        return element_variable;
    }

    public void setElement_variable(String element_variable) {
        this.element_variable = element_variable;
    }

    public String getElement_validation() {
        return element_validation;
    }

    public void setElement_validation(String element_validation) {
        this.element_validation = element_validation;
    }

    public String getMaximum() {
        return maximum;
    }

    public void setMaximum(String maximum) {
        this.maximum = maximum;
    }

    public String getMinimum() {
        return minimum;
    }

    public void setMinimum(String minimum) {
        this.minimum = minimum;
    }

    public String getCalculation_logic() {
        return calculation_logic;
    }

    public void setCalculation_logic(String calculation_logic) {
        this.calculation_logic = calculation_logic;
    }

    public String getReadonly() {
        return readonly;
    }

    public void setReadonly(String readonly) {
        this.readonly = readonly;
    }

    public String getElement_required() {
        return element_required;
    }

    public void setElement_required(String element_required) {
        this.element_required = element_required;
    }

    public String getElement_note() {
        return element_note;
    }

    public void setElement_note(String element_note) {
        this.element_note = element_note;
    }

    public String getInterlink_question_id() {
        return interlink_question_id;
    }

    public void setInterlink_question_id(String interlink_question_id) {
        this.interlink_question_id = interlink_question_id;
    }

    public List<ElementChoice> getElement_choices() {
        return element_choices;
    }

    public void setElement_choices(List<ElementChoice> element_choices) {
        this.element_choices = element_choices;
    }

    public List<BranchinglogicModal> getCause_branching_logic() {
        return cause_branching_logic;
    }

    public void setCause_branching_logic(List<BranchinglogicModal> cause_branching_logic) {
        this.cause_branching_logic = cause_branching_logic;
    }

    public List<BranchinglogicModal> getEffect_branching_logic() {
        return effect_branching_logic;
    }

    public void setEffect_branching_logic(List<BranchinglogicModal> effect_branching_logic) {
        this.effect_branching_logic = effect_branching_logic;
    }

    public String getVlookup() {
        return vlookup;
    }

    public void setVlookup(String vlookup) {
        this.vlookup = vlookup;
    }

    public String getVlookup_qustion_id() {
        return vlookup_qustion_id;
    }

    public void setVlookup_qustion_id(String vlookup_qustion_id) {
        this.vlookup_qustion_id = vlookup_qustion_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated_at() {
        return created_at;
    }

    public void setCreated_at(String created_at) {
        this.created_at = created_at;
    }

    public String getUpdated_at() {
        return updated_at;
    }

    public void setUpdated_at(String updated_at) {
        this.updated_at = updated_at;
    }

    public String getAnswers() {
        return answers;
    }

    public void setAnswers(String answers) {
        this.answers = answers;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}
