package com.ansv.taskmanagement.service.impl;

import com.ansv.taskmanagement.dto.criteria.SearchCriteria;
import com.ansv.taskmanagement.dto.response.ActivityDTO;
import com.ansv.taskmanagement.dto.specification.GenericSpecificationBuilder;
import com.ansv.taskmanagement.mapper.BaseMapper;
import com.ansv.taskmanagement.model.Activity;
import com.ansv.taskmanagement.repository.ActivityRepository;
import com.ansv.taskmanagement.service.ActivityService;
import com.ansv.taskmanagement.service.AssignmentService;
import com.ansv.taskmanagement.util.DataUtils;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class AssigmentServiceImpl implements AssignmentService {

    private static final Logger logger = LoggerFactory.getLogger(AssigmentServiceImpl.class);


}