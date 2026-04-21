package com.Sharvari.elearningPlatform.repository.impl;

import com.Sharvari.elearningPlatform.model.Question;
import com.Sharvari.elearningPlatform.model.Quiz;
import com.Sharvari.elearningPlatform.repository.QuizRepository;
import com.Sharvari.elearningPlatform.util.DBConnection;

import java.sql.*;
import java.util.*;

public class QuizRepositoryImpl implements QuizRepository {

    private Connection conn() {
        return DBConnection.getConnection();
    }
}
