package com.fkanban.fkanban.kanbans.columns;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api/kanban/{KanbanId}/column")
public class ColumnController {

    //final private ColumnService columnService

    //@GetMapping("/all")
    //@ResponseBody
    //public List<KanoTask> getAllKanoTasks(@PathVariable Long kanbanId) {
        //return columnService.getAllColumnsByKanbanId(kanbanId);
    //}
}
